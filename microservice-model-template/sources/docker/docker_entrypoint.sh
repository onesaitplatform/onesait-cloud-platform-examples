#!/bin/bash
echo "Starting service..."
echo "Starting worker"
cd flask
gunicorn -b 0.0.0.0:5000 -w 1 app:ModelController &
cd ..
echo "."
echo "Starting proxy"
java -Xms1G -Xmx3G -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar app.jar
echo "."
echo "All started!"
