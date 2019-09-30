# Model microservice example
In this project we service a Machine Learning Model.

The common API interfaz is implemented over Springboot framework as proxy to secure app.

The model is an internal/external resource called from the proxy (implemented with flask in this example).

## Docker commands
Before build image, copy neccesary files to docker folder:
> ./installation.sh

To build image use:

> docker build -t modelservice .

To run image use:

> docker run -e ONESAIT_SERVER_NAME={{ ONESAIT_SERVER_NAME }} -e OAUTH_CLIENT={{ OAUTH_CLIENT }} -e PORT={{ PORT }} -e SERVER_NAME=localhost -e CONTEXT_PATH=/{{ CONTEXT_PATH }} -e OAUTH_SECRET={{ OAUTH_SECRET }} -p {{ PORT }}:{{  PORT }} --name {{ CONTAINER_NAME }} modelservice

## API interface

Once the service is up and running, you can submit a request from the command line, e.g.:

1. To conduct a health check, test if the service is currently working:

        curl -X GET http://<host>:<port>/<modelendpoint>/api/v<version>/health
        {
            "_id":"34758ab7-87e8-4ca2-b80f-5f5ab1288a0f",
            "health":"UP"
        }

2. To check model status:

        curl -X GET http://<host>:<port>/<modelendpoint>/api/v<version>/status
        {
            "_id":"b5176766-28eb-4640-9eb1-4d0460ef5e4a",
            "is_training":false,
            "last_trained":null,
            "last_training_time":0,
            "timestamp":"2019-07-10T11:35:38.419492",
            "training_score":-1
        }

3. To check model information:

        curl -X GET http://<host>:<port>/<modelendpoint>/api/v<version>/information
        {
            "description":"Modelo de prediccion de precipitacion binario","endpoint":"PrecipitationBinaryPredictor",
            "name":"Precipitation Binary Predictor",
            "version":"0"
        }

4. To check model input sample:

        curl -X GET http://<host>:<port>/<modelendpoint>/api/v<version>/sample
        {
            "hPa":987,
            "hum":0.23,
            "tmp0":27.12,
            "tmp1":20.36
        }

5. To check model stats:

        curl -X GET http://<host>:<port>/<modelendpoint>/api/v<version>/stats
        {
            "last_trained":null,
            "last_training_time":0,
            "model":"KNeighborsClassifier(algorithm='auto', leaf_size=30, metric='minkowski',\n           metric_params=None, n_jobs=None, n_neighbors=2, p=2,\n           weights='uniform')",
            "preprocessor":"StandardScaler(copy=True, with_mean=True, with_std=True)",
            "training_score":-1
        }

6. To predict with model:

        curl -X POST -d <data_to_predict> http://<host>:<port>/<modelendpoint>/api/v<version>/predict
        {
            "label":"No precipitation",
            "prediction":0
        }

7. To start a re-train task in model:

        curl -X POST -d <options> http://<host>:<port>/<modelendpoint>/api/v<version>/train
        {
            "last_trained":null,
            "training_ontology":null,
            "training_query":"
            db.{ontology}.find()",
            "training_score":-1
        }