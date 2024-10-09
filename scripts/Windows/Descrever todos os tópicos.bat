@echo off
cd C:\Users\User\kafka_2.13-3.3.1

start cmd /k "bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --describe"
