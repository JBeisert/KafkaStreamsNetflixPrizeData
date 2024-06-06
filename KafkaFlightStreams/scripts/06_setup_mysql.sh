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

echo "Tworze Tabele"

docker exec -i mymysql mysql -ustreamuser -pstream streamdb <<EOF
CREATE TABLE IF NOT EXISTS data_sink (
  id INT PRIMARY KEY AUTO_INCREMENT,
  Title VARCHAR(255),
  ratingAmount INT,
  ratingSum INT,
  uniqueUsersCount INT
);
EOF

echo "Baza danych została skonfigurowana pomyślnie."
