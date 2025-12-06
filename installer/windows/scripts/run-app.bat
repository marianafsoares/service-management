@echo off
setlocal EnableExtensions

rem === BASE = carpeta \app del instalado ===
rem %~dp0 = carpeta donde está este .bat (por ejemplo C:\Program Files\BitsAndBytes\app\scripts\)
set "BASE=%~dp0.."

rem === JAVA 21 EXPLÍCITO (el que ya SABEMOS que funciona) ===
set "JAVA=C:\Program Files\Java\jdk-21\bin\javaw.exe"

if not exist "%JAVA%" (
  echo No se encontro Java 21 en "%JAVA%".
  pause
  exit /b 1
)

rem === DETECTAR EL JAR PRINCIPAL (primer .jar en \app) ===
set "APP_JAR="
for %%J in ("%BASE%\*.jar") do (
  set "APP_JAR=%%~nxJ"
  goto :jarFound
)

:jarFound
if not defined APP_JAR (
  echo No se encontro el JAR de la app en "%BASE%".
  pause
  exit /b 2
)

rem === EJECUTAR CON CLASSPATH lib\* + JAR ===
pushd "%BASE%"
rem Lanzar la app de manera desacoplada para que la ventana de cmd pueda cerrarse
start "" /B "%JAVA%" -Xmx1024m -Dfile.encoding=UTF-8 -cp "lib\*;%APP_JAR%" Main
popd

endlocal
exit /b 0
