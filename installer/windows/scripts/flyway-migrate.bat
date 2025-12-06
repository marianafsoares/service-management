@echo off
setlocal enabledelayedexpansion

REM Determinar la carpeta raiz de la instalacion
set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..\..") do set "INSTALL_DIR=%%~fI"
set "APP_CORE=%INSTALL_DIR%\app"

if not exist "%APP_CORE%" (
    echo No se encontro la carpeta "app" dentro de %INSTALL_DIR%.
    exit /b 1
)

set "JAVA_EXE=java"
if exist "%INSTALL_DIR%\jre\bin\java.exe" (
    set "JAVA_EXE=%INSTALL_DIR%\jre\bin\java.exe"
)

set "APP_JAR="
for %%J in ("%APP_CORE%\*.jar") do (
    set "APP_JAR=%%~nxJ"
    goto :jarFound
)

:jarFound
if not defined APP_JAR (
    echo No se encontro el archivo JAR de la aplicacion en %APP_CORE%.
    exit /b 1
)

pushd "%APP_CORE%"
"%JAVA_EXE%" -cp "lib\*;%APP_JAR%" configs.DatabaseMigration
set "EXIT_CODE=%ERRORLEVEL%"
popd

if not "%EXIT_CODE%"=="0" (
    echo Error al ejecutar las migraciones de base de datos.
) else (
    echo Migraciones ejecutadas correctamente.
)

exit /b %EXIT_CODE%
