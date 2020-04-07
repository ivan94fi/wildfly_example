#!/bin/bash

name="mariadb"

if [ ! "$(docker ps -q -f name="$name")" ];
then
    if [ "$(docker ps -aq -f status=exited -f name="$name")" ];
    then
        docker start $name
    else
        docker run -p 3306:3306 --name $name -e MYSQL_ROOT_PASSWORD=pass -d mariadb:10.4.12-bionic
    fi
else
    echo "container "$name" already running"
fi


