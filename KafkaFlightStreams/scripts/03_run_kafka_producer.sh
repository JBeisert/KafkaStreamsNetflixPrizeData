#!/bin/sh

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

java -cp /usr/lib/kafka/libs/*:KafkaProducer.jar \
com.example.bigdata.TestProducer data/netflix-prize-data 10 netflix-ratings-input \
1 "${CLUSTER_NAME}"-w-0:9092