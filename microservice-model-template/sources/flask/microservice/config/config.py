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
import logging

if os.path.abspath(os.path.dirname(os.path.dirname(__file__))) not in sys.path:
    sys.path.insert(0, os.path.abspath(os.path.dirname(os.path.dirname(__file__))))

logger = logging.getLogger("flask.app.ModelConfig")

APP_FOLDER = os.path.abspath(os.path.dirname(os.path.dirname(__file__)))


class ModelConfig:

    @property
    def _default_config(self):
        return {
            "NAME" : "Default Model Service",
            "ENDPOINT": "DefaultModelService",
            "VERSION": 0,
            "DESCRIPTION": "Default Model Service to do nothing",
            "PLATFORM_HOST": "iotbrokerservice",
            "PLATFORM_PORT": 19000,
            "PLATFORM_DIGITAL_CLIENT": "<iot_client>",
            "PLATFORM_DIGITAL_CLIENT_TOKEN": None,
            "PLATFORM_DIGITAL_CLIENT_PROTOCOL": "http",
            "PLATFORM_DIGITAL_CLIENT_AVOID_SSL_CERTIFICATE": False,
            "PLATFORM_ONTOLOGY_DATA": None,
            "PLATFORM_QUERY_DATA": "db.{ontology}.find()",
            "APP_FOLDER": APP_FOLDER,
            "PREPROCESSOR_FILE": "default_preprocessor.pkl",
            "MODEL_FILE": "default_model.pkl",
            "LABELS_FILE": "labels.json",
            "SUPERVISOR_ENDPOINT" : None,
            "LOG_FOLDER": "/var/log/platform-logs"
        }
    
    def __init__(self):
        self.load_config()

    @staticmethod
    def generate_template():
        logger.info("Generating config file template...")
        config_json_template = os.path.join(APP_FOLDER, "config", "config_template.json")
        
        with open(config_json_template, "w") as config_template_file:
            config = json.dump(ModelConfig._default_config, config_template_file, indent=4)
        logger.info("Config template generated! - {}".format(config_json_template))

    def load_config(self):
        config_json = os.path.join(APP_FOLDER, "data", "config.json")
        with open(config_json, "r") as config_file:
            config = json.load(config_file)

        for k in self._default_config.keys():
            if k not in config:
                config[k] = self._default_config[k]

        for k, v in config.items():
            if k.endswith("_FILE"):
                config[k] = os.path.join(APP_FOLDER, "data", v)

        for k, v in config.items():
            self.__setattr__(k, v)

        logger.info("Config loaded!")
                
        return config

    def save_config(self):
        config_file_new = os.path.join(APP_FOLDER, "data", "config.json")
        with open(config_file_new, "w") as config_new_file:
            config_vars = {}
            for k, v in self.__dict__.items():
                if k == k.upper():
                    config_vars[k] = v
            json.dump(config_vars, config_new_file, indent=4)
            logger.info("Saved config: {}".format(config_vars))


ModelConfig = ModelConfig()

if __name__ == '__main__':
    ModelConfig.generate_template()    


