@echo off
cd C:\Users\User\kafka_2.13-3.3.1

rem Visualiza todas as mensagens desde o início que estão no tópico
start cmd /k "bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic ECOMMERCE_NEW_ORDER --from-beginning"
