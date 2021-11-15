#!/bin/bash

# shellcheck disable=SC2046
docker rm -f $(docker ps -qa)

read -r -p "Delete volumes? [y/N] " response
case "$response" in
    [yY])
        docker volume rm -f redis_redis_data
        docker volume rm -f redis_kafka_data
        ;;
    *)
        docker volume ls
        ;;
esac

docker-compose up -d
