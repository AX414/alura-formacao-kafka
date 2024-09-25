@echo off
cd C:\Users\User\kafka_2.13-3.3.1

start cmd /k "bin\windows\kafka-topics.bat --alter --bootstrap-server localhost:9092 --topic ECOMMERCE_NEW_ORDER --partitions 3"
