#!/bin/sh
mvn clean
docker rm -f phone_shop
docker rm -f phone_shop_db
docker rm -f image_storage
docker rmi -f phone-shop-image:1.0