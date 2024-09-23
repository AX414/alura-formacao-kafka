@echo off
cd C:\Users\User\kafka_2.13-3.3.1

rem Envia mensagens para o tópico
start cmd /k "bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic ECOMMERCE_NEW_ORDER"
