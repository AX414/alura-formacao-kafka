@echo off
cd C:\Users\User\kafka_2.13-3.3.1

rem Inicia o Zookeeper
start cmd /k "bin\windows\zookeeper-server-start.bat config\zookeeper.properties"