@echo off
setlocal EnableExtensions

REM Normalizar rutas provistas por el usuario (elimina comillas sobrantes)
if defined LAUNCH4J_HOME set "LAUNCH4J_HOME=%LAUNCH4J_HOME:"=%"
if defined INNO_HOME set "INNO_HOME=%INNO_HOME:"=%"
if defined BUNDLED_JRE_SOURCE set "BUNDLED_JRE_SOURCE=%BUNDLED_JRE_SOURCE:"=%"

REM =====================================
REM Configuracion de herramientas externas
REM =====================================
:autodetect_launch4j
if not defined LAUNCH4J_HOME (
    if exist "C:\Program Files\Launch4j\launch4jc.exe" (
        set "LAUNCH4J_HOME=C:\Program Files\Launch4j"
        goto :autodetect_launch4j_done
    )
    if exist "C:\Program Files (x86)\Launch4j\launch4jc.exe" (
        set "LAUNCH4J_HOME=C:\Program Files (x86)\Launch4j"
        goto :autodetect_launch4j_done
    )
    echo [ERROR] Defini LAUNCH4J_HOME apuntando a la carpeta donde esta launch4jc.exe.
    echo En PowerShell usá: $env:LAUNCH4J_HOME="C:\\Program Files\\Launch4j" ^(ajustando la ruta si es de 32 bits^).
    exit /b 1
)
:autodetect_launch4j_done
if not exist "%LAUNCH4J_HOME%\launch4jc.exe" (
    echo [ERROR] No se encontro launch4jc.exe en "%LAUNCH4J_HOME%".
    exit /b 1
)

:autodetect_inno
if not defined INNO_HOME (
    if exist "C:\Program Files\Inno Setup 6\ISCC.exe" (
        set "INNO_HOME=C:\Program Files\Inno Setup 6"
        goto :autodetect_inno_done
    )
    if exist "C:\Program Files (x86)\Inno Setup 6\ISCC.exe" (
        set "INNO_HOME=C:\Program Files (x86)\Inno Setup 6"
        goto :autodetect_inno_done
    )
    echo [ERROR] Defini INNO_HOME apuntando a la carpeta donde esta ISCC.exe.
    echo En PowerShell usá: $env:INNO_HOME="C:\\Program Files (x86)\\Inno Setup 6" ^(ajustando la ruta segun la instalacion^).
    exit /b 1
)
:autodetect_inno_done
if not exist "%INNO_HOME%\ISCC.exe" (
    echo [ERROR] No se encontro ISCC.exe en "%INNO_HOME%".
    exit /b 1
)

REM =====================================
REM Construir la distribucion Maven
REM =====================================
set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..\..\..") do set "PROJECT_ROOT=%%~fI"
if not defined PROJECT_ROOT (
    echo [ERROR] No se pudo resolver la ubicacion del proyecto a partir del script.
    exit /b 1
)
if not exist "%PROJECT_ROOT%\pom.xml" (
    echo [ERROR] No se encontro el pom.xml del proyecto. Revisá la ubicacion del script.
    exit /b 1
)

set "DEFAULT_RUNTIME_DIR=%PROJECT_ROOT%\installer\windows\runtime"
set "PREVIOUS_DIST_JRE=%PROJECT_ROOT%\target\gestion-1.0.0\app\jre"
if not defined BUNDLED_JRE_SOURCE (
    if exist "%DEFAULT_RUNTIME_DIR%\bin\javaw.exe" (
        set "BUNDLED_JRE_SOURCE=%DEFAULT_RUNTIME_DIR%"
    ) else if exist "%PREVIOUS_DIST_JRE%\bin\javaw.exe" (
        echo [INFO] Se detecto un runtime previo en target\gestion-1.0.0\app\jre. Guardandolo antes de limpiar...
        robocopy "%PREVIOUS_DIST_JRE%" "%DEFAULT_RUNTIME_DIR%" /mir >nul
        if errorlevel 8 (
            echo [ERROR] No se pudo copiar el runtime existente a installer\windows\runtime. Codigo de error %errorlevel%.
            exit /b 1
        )
        set "BUNDLED_JRE_SOURCE=%DEFAULT_RUNTIME_DIR%"
    )
)

pushd "%PROJECT_ROOT%" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se pudo cambiar al directorio del proyecto.
    exit /b 1
)

if exist "target" rmdir /s /q "target" >nul 2>&1
call mvn -q -DskipTests clean package
if errorlevel 1 (
    popd
    echo [ERROR] Fallo el build de Maven.
    exit /b 1
)

popd
set "DIST_DIR=%PROJECT_ROOT%\target\gestion-1.0.0"
if not exist "%DIST_DIR%" (
    echo [ERROR] No se encontro la distribucion en "%DIST_DIR%".
    exit /b 1
)

REM =====================================
REM Copiar un runtime embebido si esta disponible
REM =====================================
set "RUNTIME_SOURCE=%BUNDLED_JRE_SOURCE%"
if not defined RUNTIME_SOURCE (
    set "RUNTIME_SOURCE=%PROJECT_ROOT%\installer\windows\runtime"
)
if exist "%RUNTIME_SOURCE%\bin\javaw.exe" (
    echo [INFO] Copiando runtime embebido desde "%RUNTIME_SOURCE%"...
    if exist "%DIST_DIR%\app\jre" rmdir /s /q "%DIST_DIR%\app\jre" >nul 2>&1
    robocopy "%RUNTIME_SOURCE%" "%DIST_DIR%\app\jre" /mir >nul
    if errorlevel 8 (
        echo [ERROR] No se pudo copiar el runtime embebido. Codigo de error %errorlevel%.
        exit /b 1
    )
) else (
    if defined BUNDLED_JRE_SOURCE (
        echo [ADVERTENCIA] No se encontro "%BUNDLED_JRE_SOURCE%\bin\javaw.exe". Revisá la ruta proporcionada.
    ) else (
        echo [INFO] No se encontro runtime en installer\windows\runtime. El instalador usara el Java del sistema.
    )
)

REM =====================================
REM Copiar un runtime embebido si esta disponible
REM =====================================
set "RUNTIME_SOURCE=%BUNDLED_JRE_SOURCE%"
if not defined RUNTIME_SOURCE (
    set "RUNTIME_SOURCE=%PROJECT_ROOT%\installer\windows\runtime"
)
if exist "%RUNTIME_SOURCE%\bin\javaw.exe" (
    echo [INFO] Copiando runtime embebido desde "%RUNTIME_SOURCE%"...
    if exist "%DIST_DIR%\app\jre" rmdir /s /q "%DIST_DIR%\app\jre" >nul 2>&1
    robocopy "%RUNTIME_SOURCE%" "%DIST_DIR%\app\jre" /mir >nul
    if errorlevel 8 (
        echo [ERROR] No se pudo copiar el runtime embebido. Codigo de error %errorlevel%.
        exit /b 1
    )
) else (
    if defined BUNDLED_JRE_SOURCE (
        echo [ADVERTENCIA] No se encontro "%BUNDLED_JRE_SOURCE%\bin\javaw.exe". Revisá la ruta proporcionada.
    ) else (
        echo [INFO] No se encontro runtime en installer\windows\runtime. El instalador usara el Java del sistema.
    )
)

REM =====================================
REM Copiar un runtime embebido si esta disponible
REM =====================================
set "RUNTIME_SOURCE=%BUNDLED_JRE_SOURCE%"
if not defined RUNTIME_SOURCE (
    set "RUNTIME_SOURCE=%PROJECT_ROOT%\installer\windows\runtime"
)
if exist "%RUNTIME_SOURCE%\bin\javaw.exe" (
    echo [INFO] Copiando runtime embebido desde "%RUNTIME_SOURCE%"...
    if exist "%DIST_DIR%\app\jre" rmdir /s /q "%DIST_DIR%\app\jre" >nul 2>&1
    robocopy "%RUNTIME_SOURCE%" "%DIST_DIR%\app\jre" /mir >nul
    if errorlevel 8 (
        echo [ERROR] No se pudo copiar el runtime embebido. Codigo de error %errorlevel%.
        exit /b 1
    )
) else (
    if defined BUNDLED_JRE_SOURCE (
        echo [ADVERTENCIA] No se encontro "%BUNDLED_JRE_SOURCE%\bin\javaw.exe". Revisá la ruta proporcionada.
    ) else (
        echo [INFO] No se encontro runtime en installer\windows\runtime. El instalador usara el Java del sistema.
    )
)

REM =====================================
REM Copiar un runtime embebido si esta disponible
REM =====================================
set "RUNTIME_SOURCE=%BUNDLED_JRE_SOURCE%"
if not defined RUNTIME_SOURCE (
    set "RUNTIME_SOURCE=%PROJECT_ROOT%\installer\windows\runtime"
)
if exist "%RUNTIME_SOURCE%\bin\javaw.exe" (
    echo [INFO] Copiando runtime embebido desde "%RUNTIME_SOURCE%"...
    if exist "%DIST_DIR%\app\jre" rmdir /s /q "%DIST_DIR%\app\jre" >nul 2>&1
    robocopy "%RUNTIME_SOURCE%" "%DIST_DIR%\app\jre" /mir >nul
    if errorlevel 8 (
        echo [ERROR] No se pudo copiar el runtime embebido. Codigo de error %errorlevel%.
        exit /b 1
    )
) else (
    if defined BUNDLED_JRE_SOURCE (
        echo [ADVERTENCIA] No se encontro "%BUNDLED_JRE_SOURCE%\bin\javaw.exe". Revisá la ruta proporcionada.
    ) else (
        echo [INFO] No se encontro runtime en installer\windows\runtime. El instalador usara el Java del sistema.
    )
)

REM =====================================
REM Copiar un runtime embebido si esta disponible
REM =====================================
set "RUNTIME_SOURCE=%BUNDLED_JRE_SOURCE%"
if not defined RUNTIME_SOURCE (
    set "RUNTIME_SOURCE=%PROJECT_ROOT%\installer\windows\runtime"
)
if exist "%RUNTIME_SOURCE%\bin\javaw.exe" (
    echo [INFO] Copiando runtime embebido desde "%RUNTIME_SOURCE%"...
    if exist "%DIST_DIR%\app\jre" rmdir /s /q "%DIST_DIR%\app\jre" >nul 2>&1
    robocopy "%RUNTIME_SOURCE%" "%DIST_DIR%\app\jre" /mir >nul
    if errorlevel 8 (
        echo [ERROR] No se pudo copiar el runtime embebido. Codigo de error %errorlevel%.
        exit /b 1
    )
) else (
    if defined BUNDLED_JRE_SOURCE (
        echo [ADVERTENCIA] No se encontro "%BUNDLED_JRE_SOURCE%\bin\javaw.exe". Revisá la ruta proporcionada.
    ) else (
        echo [INFO] No se encontro runtime en installer\windows\runtime. El instalador usara el Java del sistema.
    )
)

REM =====================================
REM Generar el ejecutable con Launch4j
REM =====================================
set "L4J_CONFIG=%DIST_DIR%\installer\launch4j\gestion-launch4j.xml"
if not exist "%L4J_CONFIG%" (
    echo [ERROR] No se encontro "%L4J_CONFIG%".
    exit /b 1
)

set "APP_ICON=%DIST_DIR%\installer\assets\bits-and-bytes.ico"
if not exist "%APP_ICON%" (
    echo [ERROR] No se encontro el icono "%APP_ICON%". Copia bits-and-bytes.ico antes de continuar.
    echo Si preferis no usar icono personalizado, actualiza launch4j e Inno Setup para quitar la referencia.
    exit /b 1
)

set "APP_JAR=%DIST_DIR%\app\gestion.jar"
if not exist "%APP_JAR%" (
    echo [ERROR] No se encontro el jar de la aplicacion en "%APP_JAR%".
    echo Ejecuta mvn clean package y asegurate de que la distribucion contenga app\gestion.jar.
    exit /b 1
)

set "APP_LIB_DIR=%DIST_DIR%\app\lib"
if not exist "%APP_LIB_DIR%" (
    echo [ERROR] No se encontro la carpeta de librerias en "%APP_LIB_DIR%".
    exit /b 1
)

set "BUNDLED_JAVA=%DIST_DIR%\app\jre\bin\javaw.exe"
if exist "%BUNDLED_JAVA%" (
    echo [INFO] Se detecto un runtime embebido en app\jre.
) else (
    echo [ADVERTENCIA] No se encontro app\jre\bin\javaw.exe. Launch4j usara el Java del sistema si esta disponible.
    echo             Copia un JDK/JRE 21 dentro de "%DIST_DIR%\app\jre" para distribuirlo junto con la aplicacion.
)

set "L4J_TEMPLATE=%DIST_DIR%\installer\launch4j\gestion-launch4j.xml"
set "L4J_CONFIG_RESOLVED=%DIST_DIR%\gestion-launch4j-resuelto.xml"

copy /y "%L4J_TEMPLATE%" "%L4J_CONFIG_RESOLVED%" >nul
if errorlevel 1 (
    echo [ERROR] No se pudo preparar la configuracion temporal para Launch4j.
    exit /b 1
)

pushd "%DIST_DIR%" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se pudo cambiar temporalmente al directorio de distribucion para ejecutar Launch4j.
    exit /b 1
)

"%LAUNCH4J_HOME%\launch4jc.exe" "gestion-launch4j-resuelto.xml"
set "L4J_ERROR=%errorlevel%"

popd >nul 2>&1

if %L4J_ERROR% neq 0 (
    echo [ERROR] Launch4j devolvio un error.
    exit /b %L4J_ERROR%
)

del /f /q "%L4J_CONFIG_RESOLVED%" >nul 2>&1

if not exist "%DIST_DIR%\BitsAndBytes.exe" (
    echo [ERROR] Launch4j no genero BitsAndBytes.exe. Revisa la configuracion.
    exit /b 1
)

REM =====================================
REM Ejecutar Inno Setup para crear el instalador
REM =====================================
set "INNO_SCRIPT=%DIST_DIR%\installer\inno\gestion.iss"
if not exist "%INNO_SCRIPT%" (
    echo [ERROR] No se encontro "%INNO_SCRIPT%".
    exit /b 1
)

"%INNO_HOME%\ISCC.exe" "%INNO_SCRIPT%"
if errorlevel 1 (
    echo [ERROR] Fallo la compilacion de Inno Setup.
    exit /b 1
)

set "INSTALLER=%DIST_DIR%\..\BitsAndBytesSetup.exe"
if exist "%INSTALLER%" (
    echo [OK] Instalador generado en "%INSTALLER%"
    goto :end
)

echo [ADVERTENCIA] No se encontro el ejecutable de instalacion en target\. Revisalo manualmente.

:end

endlocal
