@echo off
cd C:\Users\User\kafka_2.13-3.3.1

rem Visualiza os tópicos
start cmd /k "bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092"
