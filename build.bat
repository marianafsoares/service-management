@echo off
REM Configura JAVA_HOME y MAVEN_HOME local solo para esta terminal

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
set "MAVEN_HOME=C:\apache-maven-3.9.10"

set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo ===== USANDO JAVA =====
java -version

echo ===== USANDO MAVEN =====
mvn -v

echo ===== COMPILANDO PROYECTO =====
mvn clean install

pause
