@echo off
cd C:\Users\User\kafka_2.13-3.3.1

rem Conecta com o kafka 9092 e cria o tópico ECOMMERCE_NEW_ORDER
start cmd /k "bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVOPEDIDO"
