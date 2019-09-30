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
'''
Simple flask-based API to serve model.
'''

import os
import sys
from http import HTTPStatus
from datetime import datetime
from flask import Flask, request, jsonify, Response, make_response
from flask.logging import default_handler
import logging


if os.path.abspath(os.path.dirname(__file__)) not in sys.path:
    sys.path.insert(0, os.path.abspath(os.path.dirname(__file__)))

from microservice.rest import ModelService
from microservice.config import ModelConfig as config

# Create Flask app (Controller)
ModelController = Flask(__name__)

# Load Model (Service)
global model 
model = ModelService() 
BASE_ROOT = model.path #"/{}/api/v{}".format(model.endpoint, model.version)

# Log setup
if __name__ != '__main__':
    log_file = 'onesaitplatform-microservice-{}_{}-{:%Y-%m-%d}.log'.format(model.endpoint, model.version_str, datetime.utcnow()) 
    ModelController.logger.removeHandler(default_handler)
    gunicorn_logger = logging.getLogger('gunicorn.error')
    gunicorn_logger.setLevel(logging.INFO)
    ModelController.logger.handlers = gunicorn_logger.handlers
    ModelController.logger.setLevel(gunicorn_logger.level)
    file_handler = logging.handlers.TimedRotatingFileHandler(filename=os.path.join(config.LOG_FOLDER, log_file), when='midnight', backupCount=10)
    file_handler.setFormatter(logging.Formatter('onesaitplatform-microservice-model [%(asctime)s]  %(levelname)s  %(name)s.%(funcName)s:%(lineno)d - %(message)s'))
    file_handler.setLevel(logging.INFO)
    ModelController.logger.addHandler(file_handler)

# App Controller
@ModelController.route(BASE_ROOT + '/health', methods=['GET'])
def health():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    try:
        model.raise_ip_not_allowed(request.remote_addr)
        response = jsonify(model.health)

    except ConnectionRefusedError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response

@ModelController.route(BASE_ROOT + '/information', methods=['GET'])
def information():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    try:
        model.raise_ip_not_allowed(request.remote_addr)
        response = jsonify(model.info())

    except ConnectionRefusedError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response

@ModelController.route(BASE_ROOT + '/status', methods=['GET'])
def status():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    try:
        model.raise_ip_not_allowed(request.remote_addr)
        response = jsonify(model.status)

    except ConnectionRefusedError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response

@ModelController.route(BASE_ROOT + '/sample', methods=['GET'])
def sample():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    try:
        model.raise_ip_not_allowed(request.remote_addr)
        response = jsonify(model.sample())

    except ConnectionRefusedError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response

@ModelController.route(BASE_ROOT + '/stats', methods=['GET'])
def stats():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    try:
        model.raise_ip_not_allowed(request.remote_addr)
        response = jsonify(model.stats())

    except ConnectionRefusedError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response

@ModelController.route(BASE_ROOT + '/predict', methods=['POST'])
def predict():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    '''Predict over data'''
    try:
        body = request.get_json(force=True)

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        resp = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        resp.status_code = HTTPStatus.BAD_REQUEST
        resp.headers['Content-Type'] = 'application/json'
        return resp

    try:
        data_out = model.transform_out(model.predict(model.transform_in(body)))
        response = jsonify(data_out)

    except AssertionError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response

@ModelController.route(BASE_ROOT + "/train", methods = ["POST"])
def train():
    ModelController.logger.info("New request with ip {}: {}".format(request.remote_addr, request))
    try:
        body = request.get_json(force=True)

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        resp = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        resp.status_code = HTTPStatus.BAD_REQUEST
        resp.headers['Content-Type'] = 'application/json'
        return resp

    try:
        model.start_train(**body)
        response = jsonify(dict(
            training_score = model.training_score,
            last_trained = model.last_trained,
            training_ontology = model.training_ontology,
            training_query = model.training_query
            ))

    except ConnectionError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    except RuntimeError as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.BAD_REQUEST})
        response.status_code = HTTPStatus.BAD_REQUEST
        response.headers['Content-Type'] = 'application/json'

    except Exception as e:
        ModelController.logger.error(str(e), exc_info=True)
        response = jsonify({"message": str(e), "status_code": HTTPStatus.INTERNAL_SERVER_ERROR})
        response.status_code = HTTPStatus.INTERNAL_SERVER_ERROR
        response.headers['Content-Type'] = 'application/json'

    return response
