@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "APP_DIR=%%~fI"
for %%I in ("%APP_DIR%..") do set "INSTALL_DIR=%%~fI"

set "LOGS_BASE="
if defined LOCALAPPDATA set "LOGS_BASE=%LOCALAPPDATA%\BitsAndBytes"
if not defined LOGS_BASE if defined APPDATA set "LOGS_BASE=%APPDATA%\BitsAndBytes"
if not defined LOGS_BASE if defined USERPROFILE set "LOGS_BASE=%USERPROFILE%\BitsAndBytes"
if not defined LOGS_BASE set "LOGS_BASE=%TEMP%\BitsAndBytes"

set "LOGS_DIR=%LOGS_BASE%\logs"
set "LOG_FILE=%LOGS_DIR%\errores.log"

if not exist "%LOGS_DIR%" (
    echo No se encontro la carpeta de logs en %LOGS_DIR%.
    echo Ejecuta la aplicacion o el diagnostico una vez para generar logs.
    goto :end
)

if not exist "%LOG_FILE%" (
    echo Todavia no existe el archivo errores.log en %LOGS_DIR%.
    echo Se abrira la carpeta para que verifiques los archivos disponibles.
    start "" explorer "%LOGS_DIR%"
    goto :end
)

echo Abriendo %LOG_FILE% en el Bloc de notas...
start "" notepad "%LOG_FILE%"

echo.
:end
echo Presiona una tecla para cerrar esta ventana.
pause >nul
endlocal
