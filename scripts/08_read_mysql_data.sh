#!/bin/bash

# Nazwa kontenera MySQL
CONTAINER_NAME=mymysql

# Połącz się z kontenerem MySQL i odczytaj dane z tabeli data_sink
docker exec -i $CONTAINER_NAME mysql -ustreamuser -pstream streamdb <<EOF
SELECT * FROM data_sink;
EOF
