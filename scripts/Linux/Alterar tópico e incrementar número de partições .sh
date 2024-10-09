#!/bin/bash
cd /home/ax414/Downloads/kafka_2.13-3.8.0

bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic ECOMMERCE_NEW_ORDER --partitions 3