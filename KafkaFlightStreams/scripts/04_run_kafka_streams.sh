#!/bin/sh

D="$1"
L="$2"
O="$3"
delay="$4"

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

java -cp /usr/lib/kafka/libs/*:bigdata-2.jar \
org.example.RealTimeStreamProcessor "${CLUSTER_NAME}-w-0:9092" \
"${D}" "${L}" "${O}" "${delay}"