# Copyright Indra Soluciones Tecnologías de la Información, S.L.U.
# 2013-2019 SPAIN
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#      http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os
import sys
import json
import uuid
import time
import pickle
import joblib
from datetime import datetime
import threading
import requests
import logging
from microservice.utils.wrappers import *
from microservice.model import ModelTask
from microservice.enum import ModelTasks, FileSerializers

try:
    import urllib3
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
except:
    pass

logger = logging.getLogger("flask.app.BaseModelService")

from onesaitplatform.iotbroker import DigitalClient
from onesaitplatform.files import FileManager

if os.path.abspath(os.path.dirname(os.path.dirname(__file__))) not in sys.path:
    sys.path.insert(0, os.path.abspath(os.path.dirname(os.path.dirname(__file__))))

from microservice.config import ModelConfig as config


class BaseModelService:

    NOTIFY_STATUS_SECONDS = 50
    UPLOAD_STATUS_SECONDS = 40
    RECEIVE_TASK_SECONDS = 30
    
    def __init__(self):
        logger.info("Starting BaseModelService")
        self.__id = str(uuid.uuid4())
        self.model = None
        self.preprocessor = None
        self.labels = None
        self.digital_client = None
        self.last_trained = None
        self.last_training_time = 0
        self.training_score = -1
        self.__is_training = False
        self.training_lock = threading.Lock()
        self.start_scheduler_supervisor()
        logger.info("Started BaseModelService")

    # properties
    @property
    def id(self):
        return self.__id

    @property
    def name(self):
        return config.NAME or None

    @property
    def endpoint(self):
        return config.ENDPOINT or None

    @property
    def version(self):
        return str(config.VERSION) or None

    @property
    def version_str(self):
        return self.version if self.version.startswith("v") else "v" + self.version

    @property
    def path(self):
        return "/{}/api/{}".format(self.endpoint, self.version_str)

    @property
    def description(self):
        return config.DESCRIPTION or None

    # helpers
    def info(self):
        return dict(
            name=self.name, version=self.version,
            description=self.description, endpoint=self.endpoint
            )

    @property
    def health(self):
        return dict(health="UP", _id=self.id)

    @property
    def training_ontology(self):
        return config.PLATFORM_ONTOLOGY_DATA or None

    @property
    def training_query(self):
        return config.PLATFORM_QUERY_DATA or None

    @property
    def ontology(self):
        return config.PLATFORM_ONTOLOGY_DATA or None

    @property
    def is_training(self):
        return self.__is_training

    @is_training.setter
    def is_training(self, val):
        self.training_lock.acquire()
        self.__is_training = val
        self.notify_status()
        self.training_lock.release()

    @property
    def status(self):
        return dict(
            _id=self.id,
            is_training=self.is_training, 
            timestamp=datetime.utcnow().isoformat(),
            last_trained=self.last_trained,
            last_training_time=self.last_training_time,
            training_score=self.training_score
            )

    @property
    def supervisor_endpoint(self):
        return config.SUPERVISOR_ENDPOINT or None
    
    def sample(self):
        return {}

    # stats and status utilities
    def stats(self):
        return dict(
            preprocessor = str(self.preprocessor),
            model = str(self.model), 
            last_trained = self.last_trained,
            last_training_time = self.last_training_time,
            training_score = self.training_score
        )

    # concurrence utilities
    def is_ip_allowed(self, ip):
        allowed = False
        if not self.supervisor_endpoint:
            allowed = True
        
        elif ip in self.supervisor_endpoint:
                allowed = True

        elif ip == '127.0.0.1' or ip == '::1':
                allowed = True

        return allowed

    def raise_ip_not_allowed(self, ip):
        if not self.is_ip_allowed(ip):
            raise ConnectionRefusedError("Ip {} not allowed".format(ip))


    def start_scheduler_supervisor(self):
        if self.supervisor_endpoint:
            
            logger.info("Starting supervisor scheduler threads")
            self.notify_status()
            self.update_status()
            
            threading.Thread(target=self.notify_status_schedule).start() # status scheduling sender
            threading.Thread(target=self.update_status_schedule).start() # status scheduling update
            threading.Thread(target=self.get_task_from_supervisor_schedule).start() # tasks scheduling
            logger.info("Started supervisor scheduler threads")
        
        else:
            logger.info("Not starting scheduler for use not supervisor")

    def update_status_schedule(self):
        while self.supervisor_endpoint:
            time.sleep(self.UPLOAD_STATUS_SECONDS)
            self.update_status()

    def update_status(self):
        if self.supervisor_endpoint:

            endpoint = "{}/{}/{}/status".format(self.supervisor_endpoint, self.endpoint, self.version_str)
            headers = {
                "Content-type": "application/json"
            }
            logger.debug("Receiveing status to supervisor: {}".format(endpoint))
            try:
                response = requests.get(endpoint, headers=headers)
                if response.status_code == 200:
                    status_json = response.json()
                    self.last_trained = status_json["last_trained"]
                    self.last_training_time = status_json["last_training_time"]
                    self.training_score = status_json["training_score"]
                    logger.debug("Status received: {}".format(status_json))

                elif response.status_code == 204:
                    logger.debug("No status to upload")

                else:
                    logger.warn("Bad response from supervisor: {}".format(response))
            except:
                logger.error("No response from supervisor", exc_info=True)
            
        else:
            logger.debug("Skiping update status from supervisor")

    def notify_status_schedule(self):
        while self.supervisor_endpoint:
            time.sleep(self.NOTIFY_STATUS_SECONDS)
            self.notify_status()
    
    def notify_status(self):
        if self.supervisor_endpoint:
            
            endpoint = "{}/{}/{}/status".format(self.supervisor_endpoint, self.endpoint, self.version_str)
            data = json.dumps(self.status)
            headers = {
                "Content-type": "application/json"
            }
            logger.debug("Sending status to supervisor: {} -> {}".format(endpoint, data))
            try:
                response = requests.post(endpoint, headers=headers, data=data)
                logger.debug(response)
            except:
                logger.error("No response from supervisor", exc_info=True)
            
        else:
            logger.debug("Skiping notify to supervisor")

    def execute_task(self, task):
        logger.info("Executing task {}".format(task))
        if task.execution_task == ModelTasks.TRAIN.name:
            self.start_train()
        elif task.execution_task == ModelTasks.RELOAD.name:
            self.reload()
        else:
            raise RuntimeError("Execution task not known: {}".format(task.execution_task))

    def get_task_from_supervisor_schedule(self):
        while self.supervisor_endpoint:
            time.sleep(self.RECEIVE_TASK_SECONDS)
            self.get_task_from_supervisor()
    
    def get_task_from_supervisor(self):
        if self.supervisor_endpoint:            
            endpoint = "{}/{}/{}/task/{}".format(self.supervisor_endpoint, self.endpoint, self.version_str, self.__id)
            logger.debug("Asking to supervisor for tasks: {}".format(endpoint))
            try:
                response = requests.get(endpoint)
                if response.status_code == 200:
                    logger.debug("Task received: {}".format(response.json()))
                    mtask = ModelTask.from_json(response.json())
                    logger.info(mtask)
                    self.execute_task(mtask)

                elif response.status_code == 204:
                    logger.debug("No task to execute")

                else:
                    logger.warn("Bad response from supervisor: {}".format(response))

            except:
                logger.error("Not possible to process task", exc_info=True)
            
        else:
            logger.debug("Skiping get task from supervisor")
        
    # files utilities
    def load_preprocessor(self):
        self.preprocessor = self.autoload_file_object(pattern="_preprocessor")

    def save_preprocessor(self):
        raise NotImplementedError

    def load_model(self):
        self.model = self.autoload_file_object(pattern="_model")

    def save_model(self):
        raise NotImplementedError

    def load_labels(self):
        self.labels = None

    def save_labels(self):
        raise NotImplementedError
    
    def load_other_files(self):
        raise NotImplementedError

    def save_other_files(self):
        raise NotImplementedError

    def reload(self):
        logger.info("Start reloading")
        self.load_preprocessor()
        self.load_model()
        self.load_labels()
        self.load_other_files()
        logger.info("End reloading")

    def autoload_file_object(self, pattern="_preprocessor"):
        pattern_file = self.search_pattern_file(pattern=pattern)
        loaded_object = None
        if pattern_file:
            if pattern_file.lower().endswith(".pkl"):
                loaded_object = self._load_serialized_file(pattern_file, FileSerializers.PICKLE)

            elif pattern_file.lower().endswith(".joblib"):
                loaded_object = self._load_serialized_file(pattern_file, FileSerializers.JOBLIB)

            elif pattern_file.lower().endswith(".json"):
                loaded_object = self._load_serialized_file(pattern_file, FileSerializers.JSON)

            else:
                logger.error("Not possible to autoload file {} (unknown extension)".format(pattern_file))
                raise NotImplementedError("Not serializer found for file extension {}, please overwrite load_*() method to implement it".format(pattern_file))

        else:
            logger.warn("Not possible to find file with pattern {}".format(pattern))

        return loaded_object

    def _load_serialized_file(self, file_path=None, serializer=FileSerializers.PICKLE):
        loaded_object = None
        logger.info("loading serialized file {} with serializer {}".format(file_path, serializer))

        if file_path and isinstance(serializer, FileSerializers):
            if serializer == FileSerializers.PICKLE:
                loaded_object = pickle.load(open(file_path,'rb'))

            elif serializer == FileSerializers.JOBLIB:
                loaded_object = joblib.load(file_path)

            elif serializer == FileSerializers.JSON:
                with open(file_path) as json_file:  
                    loaded_object = json.load(json_file)

            else:
                logger.warn("Not possible to load serialized file with values {} - {}".format(file_path, serializer))

        else:
            logger.warn("Not possible to load serialized file with values {} - {}".format(file_path, serializer))

        return loaded_object
    
    def search_pattern_file(self, pattern="_model", folder=None):
        res_file_path = None
        if not folder:
            folder = os.path.join(config.APP_FOLDER, "data")

        if os.path.exists(folder):
            for root, dirs, files in os.walk(folder):
                for f in files:
                    if pattern in f:
                        res_file_path = os.path.join(root, f)

        return res_file_path

    # prediction utilities
    def transform_in(self, in_data):
        raise NotImplementedError

    def predict(self, X, names, meta=None):
        raise NotImplementedError

    def transform_out(self, X):
        raise NotImplementedError

    # train utilities
    def set_digital_client(
        self, host=config.PLATFORM_HOST, port=config.PLATFORM_PORT, 
        iot_client=config.PLATFORM_DIGITAL_CLIENT,
        iot_client_token=config.PLATFORM_DIGITAL_CLIENT_TOKEN,
        protocol=config.PLATFORM_DIGITAL_CLIENT_PROTOCOL,
        avoid_ssl_certificate = config.PLATFORM_DIGITAL_CLIENT_AVOID_SSL_CERTIFICATE
        ):

        self.digital_client = DigitalClient(
            host=host, port=port, iot_client=iot_client, iot_client_token=iot_client_token
        )
        self.digital_client.protocol = protocol
        self.digital_client.avoid_ssl_certificate = avoid_ssl_certificate
        logger.info("Setted digital client: {}".format(self.digital_client))
        
    # train utilities
    @not_while_training
    def start_train(self, **kwargs):
        logger.info("Start training with args: {}".format(kwargs))
        threading.Thread(target=self.train, kwargs=kwargs).start()
    
    @not_while_training
    def train(self, ontology=None, query=None, query_type=None, query_batch_size=900, iot_broker_client_token=None):
        raise NotImplementedError
