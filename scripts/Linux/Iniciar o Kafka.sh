#!/bin/bash

# Navega até o diretório do Kafka
cd /home/ax414/Downloads/kafka_2.13-3.8.0

# Inicia o Zookeeper
bin/kafka-server-start.sh config/server.properties