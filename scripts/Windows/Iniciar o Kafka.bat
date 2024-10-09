@echo off
cd C:\Users\User\kafka_2.13-3.3.1

rem Inciando o kafka
start cmd /k "bin\windows\kafka-server-start.bat config\server.properties"
