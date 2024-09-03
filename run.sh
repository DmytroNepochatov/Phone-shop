#!/bin/sh
mvn clean package
docker-compose up --build -d
docker cp ./products/. image_storage:/usr/share/nginx/images/