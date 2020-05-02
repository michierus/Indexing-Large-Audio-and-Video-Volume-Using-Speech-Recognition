#! /bin/bash

sudo systemctl start elasticsearch.service
sudo systemctl stop elasticsearch.service
sudo systemctl status elasticsearch.service

sudo systemctl daemon-reload

sudo systemctl start zookeeper
sudo systemctl stop zookeeper
sudo systemctl status zookeeper

sudo systemctl start kafka
sudo systemctl stop kafka
sudo systemctl status kafka

curl -X GET "http://localhost:9200/?pretty"
curl -X GET "http://35.199.111.159:9200/?pretty"

curl -XGET "http://localhost:9200/audio-index/_count" -H "Content-Type: application/json" -d "   {      \"query\": {        \"match_all\": {}      }   }"
curl -XGET "http://34.67.213.110:9200/audio-index/_count" -H "Content-Type: application/json" -d "   {      \"query\": {        \"match_all\": {}      }   }"

curl -XDELETE "http://localhost:9200/audio-index"

cd /usr/local/kafka/config
-- Updata server.properties file on Kafka config folder, to allow connect on kafka outsie the server
advertised.listeners=PLAINTEXT://34.67.213.110:9092
zookeeper.connect=34.67.213.110:2181

sudo systemctl daemon-reload
sudo systemctl start kafka

cd /usr/local/kafka
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 8 --topic audio-topic
bin\windows\kafka-topics.bat --create --zookeeper 35.193.157.66:2181 --replication-factor 1 --partitions 150 --topic audio-topic

bin/kafka-topics.sh --list --zookeeper 34.67.213.110:2181
bin\windows\kafka-topics.bat --list --zookeeper 35.199.111.159:2181

-- Increase partition
bin\windows\kafka-topics.bat --alter --zookeeper localhost:2181 --topic audio-topic --partitions 24

-- Describe
bin/kafka-topics.sh --bootstrap-server 34.67.213.110:9092 --describe

-- Count qtd in topic
bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list 34.67.213.110:9092 --topic audio-topic --time -1
bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list 34.67.213.110:9092 --topic audio-topic --time -1 --offsets 1 | awk -F  ":" '{sum += $3} END {print sum}'
bin/kafka-run-class.sh kafka.admin.ConsumerGroupCommand --group mestrado.projeto.processor --bootstrap-server 34.67.213.110:9092 --describe

--Pegar a quantidade que falta
bin/kafka-run-class.sh kafka.admin.ConsumerGroupCommand --group mestrado.projeto.processor --bootstrap-server 34.67.213.110:9092 --describe | awk -F  " " '{sum += $6} END {print sum}'

ssh michieru@35.199.111.159

Check initialization status logs,
Ubuntu: /var/log/syslog
nano /var/log/syslog

--Mmake file executable
chmod +x test.sh

--Execute consumer
sudo java -jar -server -XX:+UseConcMarkSweepGC -Xms512m -Xmx1024m processador-0.0.1.jar

-- Linux commands
--CPU Utilization
top
htop

--Memory utilization
free -m

--Disk utilization
df -h

--Schedule command to be executed,
sudo apt-get install at

sudo at 20:46 -f consumer.sh
sudo at 20:46 -f producer.sh

sudo at now + 2 minutes -f consumer.sh

-- Create the script
cat <<EOF > consumer.sh
#! /bin/bash
sudo java -jar -server -XX:+UseConcMarkSweepGC -Xms512m -Xmx1024m processador-0.0.1.jar
EOF

sudo at now + 5 minutes -f consumer.sh

cat <<EOF > producer.sh
#! /bin/bash
sudo java -jar -server producer-0.0.1.jar
EOF




--see the jobs in the list
atq

--Remove from list, number 3 is the id of job
atrm 3

usar htop para ver os processos e filtrar por "sudo java" ele vai mostrar os processos

-- copy file x times
seq 16363 | xargs -I AA cp audio.wav audioAA.wav

seq 10 | xargs -I AA cp audio.wav audioAA.wav


