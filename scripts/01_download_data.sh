#!/bin/sh

BUCKET_NAME="$1"

echo "Copying flight-data-processing-kafka-streams.jar from bucket"
hadoop fs -copyToLocal gs://"$BUCKET_NAME"/bigdata-2.jar

echo "Copying KafkaProducer.jar from bucket"
hadoop fs -copyToLocal gs://"$BUCKET_NAME"/KafkaProducer.jar

echo "Copying netflix-prize-data.zip from bucket"
hadoop fs -copyToLocal gs://"$BUCKET_NAME"/netflix-prize-data.zip

echo "Copying movie_titles folder from bucket"
hadoop fs -copyToLocal gs://"$BUCKET_NAME"/movie_titles

echo "Preparing folders for data"
mkdir data
mv netflix-prize-data.zip/ data/
mv movie_titles/ data/

echo "Unzipiping flight files"
unzip netflix-prize-data.zip -d data/

echo "Cleaning up"
rm -rf netflix-prize-data.zip

echo "Done"

echo "============================================"
echo "Jeśli skrypt nie zadziałał to znaczy że nie podałeś swojego BUCKET_NAME jako argumentu"
