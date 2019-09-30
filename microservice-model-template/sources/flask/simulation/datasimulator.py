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

import random
import math


class Simulator:

    schema = {"tmp0":13.0, "tmp1":13.0, "hPa":1013, "hum":10, "pp":2000}
    
    @staticmethod
    def simulate(n):
        data = []
        for i in range(n):
            random_data = Simulator.__randomize_data(dict(Simulator.schema))
            data.append(random_data)

        return data

    @staticmethod
    def __randomize_data(data_schema):
        new_schema = {}
        
        tmp0 = data_schema["tmp0"]
        tmp0 *= random.uniform(0, 3.5)
        tmp0 = round(tmp0, 2)
        new_schema["tmp0"] = tmp0

        tmp1 = data_schema["tmp1"]
        tmp1 = tmp0 * random.uniform(0.7, 1.3)
        tmp1 = round(tmp1, 2)
        new_schema["tmp1"] = tmp1

        hpa = data_schema["hPa"]
        hpa *= random.uniform(0.9, 1.1)
        hpa = int(hpa)
        new_schema["hPa"] = hpa

        hum = data_schema["hum"]
        hum = round(random.uniform(0, 1), 2)
        new_schema["hum"] = hum

        pp = data_schema["pp"]
        pp *= random.uniform(0, 2)
        pp = int(pp)
        if tmp1 > Simulator.rain_point(tmp0, hum) + random.uniform(2, 5) and hum < 85:
            pp = 0
        new_schema["pp"] = pp

        return new_schema

    @staticmethod
    def rain_point(teperature_celsius, relative_humidity):
        if relative_humidity == 0:
            relative_humidity = 0.00001
        return teperature_celsius + 35 * math.log(relative_humidity)
        
