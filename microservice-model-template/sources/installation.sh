: Copy the result target to devops image folder and the worker app
echo "..............................................."
echo "Copying jar to docker folder to deploy"
cp -a ./target/model-manager-service-0.0.1.jar ./docker/model-manager-service-0.0.1.jar
echo "."
echo "Copying flask folder to docker folder to deploy"
cp -r ./flask ./docker/flask
echo "."
echo "...........................................done!"