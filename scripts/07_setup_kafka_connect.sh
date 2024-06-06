#!/bin/bash

# Zmienne środowiskowe
CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
BOOTSTRAP_SERVER="${CLUSTER_NAME}-w-0:9092"
TOPIC_NAME="etl-output"

# 1. Pobierz sterownik JDBC dla bazy danych MySQL
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

# 2. Skopiuj pobrany sterownik do bibliotek Kafki
sudo cp mysql-connector-j-8.0.33.jar /usr/lib/kafka/libs

# 3. Pobierz wtyczkę JDBC Sink Connector
wget https://packages.confluent.io/maven/io/confluent/kafka-connect-jdbc/10.7.0/kafka-connect-jdbc-10.7.0.jar

# 4. Utwórz katalog dla wtyczek, a następnie skopiuj pobrany plik do jego wnętrza
sudo mkdir -p /usr/lib/kafka/plugin
sudo cp kafka-connect-jdbc-10.7.0.jar /usr/lib/kafka/plugin

# 5. Utwórz plik konfiguracyjny connect-standalone.properties
cat <<EOF | sudo tee /usr/lib/kafka/config/connect-standalone.properties
plugin.path=/usr/lib/kafka/plugin
bootstrap.servers=${BOOTSTRAP_SERVER}
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
key.converter.schemas.enable=false
value.converter.schemas.enable=true
offset.storage.file.filename=/tmp/connect.offsets
offset.flush.interval.ms=10000
EOF

# 6. Utwórz plik konfiguracyjny connect-jdbc-sink.properties
cat <<EOF | sudo tee /usr/lib/kafka/config/connect-jdbc-sink.properties
# Konfiguracja połączenia z bazą danych MySQL
connection.url=jdbc:mysql://localhost:6033/streamdb
connection.user=streamuser
connection.password=stream

# Konfiguracja zadania, które kopiuje dane z Kafki do MySQL
tasks.max=1
name=kafka-to-mysql-task
connector.class=io.confluent.connect.jdbc.JdbcSinkConnector
topics=${TOPIC_NAME}
table.name.format=data_sink
delete.enabled=true
pk.mode=record_key
pk.fields=id
insert.mode=upsert
EOF

# 7. Skopiuj do odpowiedniego pliku parametry log4j
sudo cp /usr/lib/kafka/config/tools-log4j.properties /usr/lib/kafka/config/connect-log4j.properties

# 8. Dodaj linię, ograniczającą ostrzeżenia pochodzące z klasy org.reflections.Reflections
echo "log4j.logger.org.reflections=ERROR" | sudo tee -a /usr/lib/kafka/config/connect-log4j.properties

# 9. Uruchom Kafka Connect w trybie standalone
/usr/lib/kafka/bin/connect-standalone.sh /usr/lib/kafka/config/connect-standalone.properties /usr/lib/kafka/config/connect-jdbc-sink.properties
