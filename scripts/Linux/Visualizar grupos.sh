#!/bin/bash
cd /home/ax414/Downloads/kafka_2.13-3.8.0

bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe