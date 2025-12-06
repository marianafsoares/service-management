@echo off
setlocal EnableExtensions

rem ====== RUTAS BASE ======
for %%I in ("%~dp0..") do set "APP_DIR=%%~fI"
for %%I in ("%~dp0..\..") do set "INSTALL_DIR=%%~fI"

set "JAVA_EXE="
set "JAVA_SOURCE="
set "EXIT_CODE=0"

set "LOGS_BASE=%LOCALAPPDATA%\BitsAndBytes"
if not defined LOCALAPPDATA set "LOGS_BASE=%TEMP%\BitsAndBytes"
set "LOGS_DIR=%LOGS_BASE%\logs"
if not exist "%LOGS_DIR%" mkdir "%LOGS_DIR%" >nul 2>nul
set "LOG_FILE=%LOGS_DIR%\errores.log"

echo.
echo === Detectando Java 21+ (modo simple) ===

rem ====== 1) TUS DOS RUTAS JDK 21 CONOCIDAS ======
if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
  set "JAVA_EXE=C:\Program Files\Java\jdk-21\bin\java.exe"
  set "JAVA_SOURCE=C:\Program Files\Java\jdk-21"
  goto found_java
)

if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot\bin\java.exe" (
  set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot\bin\java.exe"
  set "JAVA_SOURCE=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
  goto found_java
)

rem ====== 2) JRE embebida (si existiera) ======
if exist "%INSTALL_DIR%\jre\bin\java.exe" (
  set "JAVA_EXE=%INSTALL_DIR%\jre\bin\java.exe"
  set "JAVA_SOURCE=embedded JRE"
  goto found_java
)

rem ====== 3) JAVA_HOME ======
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
  set "JAVA_SOURCE=JAVA_HOME"
  goto found_java
)

rem ====== 4) PATH (sin parsear version, solo para tener algo) ======
where java >nul 2>nul
if not errorlevel 1 (
  for %%P in (java.exe) do (
    set "JAVA_EXE=%%~$PATH:P"
    if defined JAVA_EXE (
      set "JAVA_SOURCE=PATH"
      goto found_java
    )
  )
)

echo.
echo No se encontro Java 21 en rutas conocidas.
echo Instala Temurin 21 o ajusta JAVA_HOME. 
set "EXIT_CODE=10"
goto fin

:found_java
echo Java seleccionado: "%JAVA_EXE%"
if defined JAVA_SOURCE echo Origen: %JAVA_SOURCE%

rem ====== Buscar el JAR de la app ======
set "APP_JAR="
for %%J in ("%INSTALL_DIR%\app\*.jar") do (
  set "APP_JAR=%%~nxJ"
  goto jarFound
)
:jarFound
if not defined APP_JAR (
  echo No se encontro el JAR en "%INSTALL_DIR%\app".
  set "EXIT_CODE=1"
  goto fin
)

echo.
pushd "%INSTALL_DIR%\app" >nul
"%JAVA_EXE%" -version
"%JAVA_EXE%" -cp "lib\*;%APP_JAR%" Main --diagnostico
set "EXIT_CODE=%ERRORLEVEL%"
popd >nul

echo.
if "%EXIT_CODE%"=="0" (
  echo El diagnostico finalizo correctamente.
) else (
  echo El diagnostico finalizo con codigo %EXIT_CODE%.
  echo Consulta "%LOG_FILE%" para mas detalles.
)

:fin
echo.
pause
endlocal & exit /b %EXIT_CODE%
