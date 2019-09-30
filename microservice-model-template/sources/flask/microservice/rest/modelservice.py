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
import time
import pickle
import logging
from datetime import datetime
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier
from microservice.base import BaseModelService
from microservice.utils.wrappers import *

logger = logging.getLogger("flask.app.ModelService")

if os.path.abspath(os.path.dirname(os.path.dirname(__file__))) not in sys.path:
    sys.path.insert(0, os.path.abspath(os.path.dirname(os.path.dirname(__file__))))

from microservice.config import ModelConfig as config


class ModelService(BaseModelService):

    def __init__(self):
        super().__init__()
        self.load_model()
        self.load_preprocessor()
        self.load_labels()
        self.set_digital_client()
    
    # helpers
    def sample(self):
        return {'tmp0': 27.12, 'tmp1': 20.36, 'hPa': 987, 'hum': 0.23}

    # files utilities
    def load_model(self):
        self.model = pickle.load(open(config.MODEL_FILE,'rb'))

    def save_model(self):
        pickle.dump(self.model, open(config.MODEL_FILE, 'wb'))

    def lload_preprocessor(self):
        self.preprocessor = pickle.load(open(config.PREPROCESSOR_FILE, 'rb'))
    
    def save_preprocessor(self):
        pickle.dump(self.preprocessor, open(config.PREPROCESSOR_FILE, 'wb'))

    def load_labels(self):
        with open(config.LABELS_FILE) as json_file:  
            self.labels = json.load(json_file)

    def save_labels(self):
        with open(config.LABELS_FILE, 'w') as outfile:  
            json.dump(self.labels, outfile)

    def reload(self):
        self.load_preprocessor()
        self.load_model()
        self.load_labels()

    # prediction utilities
    @logged
    def transform_in(self, in_data: dict):
        sorted_cols = ['hPa', 'hum', 'tmp0', 'tmp1']
        assert set(sorted_cols) == (set(in_data.keys())), "Invalid input data"
        in_array = np.array([in_data[col] for col in sorted_cols]).reshape(1, 4)
        return self.preprocessor.transform(in_array)
    
    @logged
    def predict(self, X, names = None, meta=None):
        return self.model.predict(X)

    @logged
    def transform_out(self, X):
        prediction = int(X[0])
        return dict(prediction=prediction, label=self.labels[str(prediction)])
        
    @logged
    def train(self, ontology=None, query=None, query_type=None, query_batch_size=900, digital_client=None, digital_client_token=None):
        # validate args
        ontology = ontology or self.training_ontology
        if not ontology:
            raise ValueError("Invalid input argument: ontology")
         
        if not query:
            query = config.PLATFORM_QUERY_DATA.format(ontology=ontology)

        if not query_type:
            query_type = "SQL" if query.lower().startswith("select") else "NATIVE"

        if digital_client:
            self.digital_client.iot_client = digital_client
        
        if digital_client_token:
            self.digital_client.iot_client_token = digital_client_token

        if self.is_training:
            raise RuntimeError("Not possible to train service while training")

        self.is_training = True
        logger.info("Train arguments: {}".format([ontology, query, query_type, query_batch_size, digital_client, digital_client_token]))

        if not self.digital_client.is_connected:
            ok_join, res_join = self.digital_client.join()
            logger.info("Train client joined with client: {} - {}".format(ok_join, res_join))

        # get data
        logger.info("Train start getting data")
        ok_query, res_query = self.digital_client.query_batch(ontology, query, query_type, batch_size = query_batch_size)
        logger.info("Train query data received: {}".format(ok_query))
        if not ok_query:
            self.is_training = False
            raise ConnectionError("Not possible to get data from server with Digital Client: {}".format(self.digital_client.to_json()))
    
        try:    
            # preprocess data
            start_time = time.time()
            data_formated = [d["predictionmodel"] for d in res_query]
            res_query = None
            df = pd.DataFrame(data_formated)
            df["pp_target"] = df["pp"] > 0
            df_processed = df.drop("pp", axis=1)
            df = None
            
            # train test split
            train, test = train_test_split(df_processed, test_size=0.2)
            
            col_target = "pp_target"
            cols_features = [c for c in list(df_processed.columns) if c != col_target]
            
            X_train = np.array(train[cols_features])
            y_train = np.array(train[[col_target]])
            X_test = np.array(test[cols_features])
            y_test = np.array(test[[col_target]])
            train = test = None

            # train preprocessor
            self.preprocessor.fit(X_train)
            X_train = self.preprocessor.transform(X_train)
            X_test = self.preprocessor.transform(X_test)

            # train model
            self.model.fit(X_train, y_train)

            # score training
            self.training_score = self.model.score(X_test, y_test)

            end_time = time.time()
            self.last_training_time = end_time - start_time
            self.last_trained = datetime.utcnow().isoformat()
            X_train = y_train = X_test = y_test = None

            self.save_preprocessor()
            self.save_model()

            logger.info("End training succesfully!")

        except Exception as e:
            logger.error("Error while trying train", exc_info=True)
            self.is_training = False
            raise e

        self.is_training = False

