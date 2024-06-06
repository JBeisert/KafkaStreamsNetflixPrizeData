#!/bin/bash

mkdir -p /tmp/datadir

docker run --name mymysql -v /tmp/datadir:/var/lib/mysql -p 6033:3306 \
-e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:debian

echo "Czekam na uruchomienie kontenera MySQL..."
sleep 20

docker exec -i mymysql mysql -uroot -pmy-secret-pw <<EOF
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS streamdb CHARACTER SET utf8;
GRANT ALL ON streamdb.* TO 'streamuser'@'%';
EOF

echo "Usuwam tabelę, jeśli istnieje i tworzę nową"

docker exec -i mymysql mysql -ustreamuser -pstream streamdb <<EOF
DROP TABLE IF EXISTS data_sink;
CREATE TABLE data_sink (
  id_AI INT PRIMARY KEY AUTO_INCREMENT,
  id INT,
  Title VARCHAR(255),
  ratingAmount INT,
  ratingSum INT,
  uniqueUsersCount INT
);
EOF

echo "Baza danych została skonfigurowana pomyślnie."
