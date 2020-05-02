#! /bin/bash

echo 'Elasticsearch install'
sudo apt-get install -y apt-transport-https
wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
add-apt-repository "deb https://artifacts.elastic.co/packages/7.x/apt stable main"
sudo apt-get install -y elasticsearch

sed -i 's/#network.host:.*/network.host: 127.0.0.1/g' /etc/elasticsearch/elasticsearch.yml
sed -i 's/#http.port:.*/http.port: 9200\nhttp.host: 0.0.0.0/g' /etc/elasticsearch/elasticsearch.yml

echo 'Start elasticsearch service'
sudo /bin/systemctl enable elasticsearch.service
sudo systemctl start elasticsearch.service

curl -XPUT -H "Content-Type: application/json" http://localhost:9200/audio-index

curl -XPUT -H "Content-Type: application/json" http://localhost:9200/audio-index/_mapping -d "{  \"properties\": {    \"fileAttributes\": {      \"type\": \"object\",      \"properties\": {\"fileName\": {  \"type\": \"text\"},\"filePath\": {  \"type\": \"text\"},\"creationTime\": {  \"type\": \"text\"},\"size\": {  \"type\": \"integer\"},\"owner\": {  \"type\": \"text\"},\"uuid\":{\"type\": \"text\"}      }    },    \"words\": {      \"type\": \"nested\",      \"properties\": {\"word\": {  \"type\": \"text\"},\"occurrences\": {  \"type\": \"text\"},\"occurrenceQuantity\": {  \"type\": \"integer\"}      }    }  }}"

curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_cluster/settings -d "{ \"transient\": { \"cluster.routing.allocation.disk.threshold_enabled\": false } }"
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_all/_settings -d "{\"index.blocks.read_only_allow_delete\": null}"