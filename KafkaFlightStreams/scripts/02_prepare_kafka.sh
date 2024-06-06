#!/bin/sh

set -e

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

echo "Checking if kafka topics already exist"
kafka-topics.sh --delete --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --topic netflix-ratings-input || true
kafka-topics.sh --delete --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --topic movie-info-input || true
kafka-topics.sh --delete --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --topic etl-output || true
kafka-topics.sh --delete --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --topic anomaly-output || true

echo "Creating kafka topics"
kafka-topics.sh --create --topic movie-info-input --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --replication-factor 1 --partitions 1
kafka-topics.sh --create --topic etl-output --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --replication-factor 1 --partitions 1
kafka-topics.sh --create --topic netflix-ratings-input --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --config cleanup.policy=compact --replication-factor 1 --partitions 1
kafka-topics.sh --create  --topic anomaly-output --bootstrap-server "${CLUSTER_NAME}"-w-0:9092 --replication-factor 1 --partitions 1

echo "Done"
