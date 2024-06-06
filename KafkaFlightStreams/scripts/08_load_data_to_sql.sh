CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

kafka-console-producer.sh \
--broker-list "${CLUSTER_NAME}"-w-0:9092 \
--topic etl-output --property parse.key=true --property key.separator=";"