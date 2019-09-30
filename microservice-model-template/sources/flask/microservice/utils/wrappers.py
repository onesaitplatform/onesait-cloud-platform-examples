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

import logging

logger = logging.getLogger("flask.app.wrappers")


def not_while_training(func):
    def call(*args, **kwargs):      
        args = list(args)
        model_service_class = args[0]
        if model_service_class.is_training:
            raise RuntimeError("Not possible to train service while training")
        return func(*args, **kwargs)   
    return call

def notify_status(func):
    def call(*args, **kwargs):      
        args = list(args)
        model_service_class = args[0]
        model_service_class.notify_status()
        res = func(*args, **kwargs)   
        model_service_class.notify_status()
        return res
    return call

def update_status(func):
    def call(*args, **kwargs):      
        args = list(args)
        model_service_class = args[0]
        model_service_class.update_status()
        res =  func(*args, **kwargs)  
        model_service_class.notify_status()
        return res
    return call

def trace_in(func, *args, **kwargs):
	logger.info("Entering function {} with args {} and kwargs {}".format(func.__name__, args, kwargs))

def trace_out(func, result):
	logger.info("Leaving function {} with result {}".format(func.__name__, result))

def logged(func):
    """ Decorator """
    def call(*args, **kwargs):
        """ The magic happens here """
        trace_in(func, *args, **kwargs)
        result = func(*args, **kwargs)
        trace_out(func, result)
        return result
    return call
