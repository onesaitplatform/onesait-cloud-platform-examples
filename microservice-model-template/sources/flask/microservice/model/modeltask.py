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

import json
import uuid
from datetime import datetime

class ModelTask:

    def __init__(self):
        self._id = str(uuid.uuid4())
        self.execution_task = None
        self.execution_policy = None
        self.timestamp = datetime.now().isoformat()


    @staticmethod
    def from_json(json_task):
        model_task = None
        try:
            if isinstance(json_task, str):
                json_task = json.loads(json_task)

            model_task = ModelTask()
            model_task._id = json_task["_id"]
            model_task.execution_task = json_task["execution_task"]
            model_task.execution_policy = json_task["execution_policy"]
            model_task.timestamp = json_task["timestamp"]
        except:
            pass

        return model_task
