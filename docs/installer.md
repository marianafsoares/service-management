# Instalador de Windows

Esta guía describe cómo generar la distribución de la aplicación, crear el ejecutable con Launch4j y empaquetarlo en un instalador de Inno Setup. Todos los comandos se ejecutan desde la raíz del repositorio.

## Guía rápida (paso a paso)

1. **Limpiá la carpeta de trabajo**: eliminá cualquier `target/` anterior para empezar desde cero.
2. **Compilá el proyecto** ejecutando `mvn clean package` en la raíz del repositorio (requiere Java 21 y Maven instalados).
3. **Copiá tu ícono** `bits-and-bytes.ico` dentro de `target/gestion-1.0.0/installer/assets/`.
4. **(Opcional, recomendado) Prepará un runtime Java 21** copiándolo a `installer/windows/runtime/` (o definiendo la variable `BUNDLED_JRE_SOURCE` con la ruta a tu runtime). El script lo replicará automáticamente dentro de la distribución.
5. **En Windows, definí las variables** `LAUNCH4J_HOME` e `INNO_HOME` apuntando a las instalaciones de Launch4j e Inno Setup (o asegurate de que estén en las rutas por defecto).
6. **Ejecutá** `installer\windows\scripts\build-installer.bat` desde la raíz del repo usando una consola de Windows.
7. **Verificá la salida**: al finalizar deben existir `target/gestion-1.0.0/BitsAndBytes.exe` y `target/BitsAndBytesSetup.exe`.

Si preferís realizar cada etapa manualmente, seguí leyendo desde la sección siguiente para ver el detalle completo.

## 1. Generar la distribución Maven

1. Asegurate de tener Java 21 y Maven instalados.
2. Ejecutá el build:
   ```bash
   mvn clean package
   ```
   Se generan dos artefactos en `target/`:
   - `gestion.jar`: el JAR principal con el manifest apuntando a `Main`.
   - `gestion-1.0.0/`: carpeta con la distribución:
     - `app/` contiene el JAR y las dependencias en `lib/`.
     - `config/` contiene `app.properties` listo para edición post-instalación.
     - `config/db/migration/` replica las migraciones de Flyway.
     - `app/scripts/flyway-migrate.bat` ejecuta las migraciones usando el mismo classpath que la aplicación.
     - `installer/` incluye las configuraciones base de Launch4j e Inno Setup.
     - `installer/assets/` es donde debes colocar `bits-and-bytes.ico` antes de compilar.
     - `docs/` incorpora esta guía y el script `mysql_grant_gestion_system.sql`.

> **Nota:** Si querés empaquetar un Java embebido, copiá tu runtime una sola vez dentro de `installer/windows/runtime/` (o apunta `BUNDLED_JRE_SOURCE` a una carpeta externa que contenga `bin/javaw.exe`). El script lo sincroniza en cada ejecución con `target/gestion-1.0.0/app/jre`, por lo que no se pierde aunque se limpie `target/`. Incluso si preferís pegar el runtime directamente en `target/gestion-1.0.0/app/jre` antes de lanzar el script, éste lo detecta, lo guarda en `installer/windows/runtime/` automáticamente y recién después limpia `target/`.

### Preparar un runtime 21 embebido (opcional pero recomendado)

1. Descargá un JDK/JRE 21 de [Adoptium](https://adoptium.net/) o generá uno recortado con `jlink` (`jlink --output runtime --add-modules java.se,jdk.crypto.ec`).
2. Copiá el contenido del runtime dentro de `installer/windows/runtime/` (o dejá el runtime en otra carpeta y definí `BUNDLED_JRE_SOURCE` con esa ruta). Debe existir `bin/javaw.exe` dentro de la carpeta que apuntes.
3. Ejecutá `installer/windows/scripts/build-installer.bat`. El script clona el runtime hacia `target/gestion-1.0.0/app/jre` cada vez antes de llamar a Launch4j e Inno Setup.
4. Si preferís usar el Java del sistema, simplemente omití este paso: el `.exe` intentará usar `app\jre` y, si no existe, caerá en el Java instalado en la PC del usuario.

## 2. Generar el `.exe` con Launch4j

1. Instalá Launch4j en tu equipo Windows.
2. Copiá tu ícono `bits-and-bytes.ico` a `target/gestion-1.0.0/installer/assets/`.
3. Abrí `target/gestion-1.0.0/installer/launch4j/gestion-launch4j.xml` desde Launch4j (no hace falta moverlo de carpeta).
4. Confirmá las rutas principales (pensadas para que el `.exe` siempre resuelva archivos relativos a su propia carpeta):
   - **Jar**: `app\gestion.jar`.
   - **Output file**: `BitsAndBytes.exe` (quedará junto al JAR en `target/gestion-1.0.0`).
   - **Classpath**: `app\gestion.jar`, `app\lib\*` y `Main` como clase principal.
   - **Application icon**: `installer\assets\bits-and-bytes.ico` (se habilita cuando copiaste el ícono).
   - **Bundled JRE path**: `app\jre` para que el `.exe` use el runtime embebido relativo (`app\jre\bin\javaw.exe`).
   - **Working dir (Chdir)**: `.` para mantener el directorio raíz de instalación como carpeta de trabajo (así la aplicación sigue encontrando `config/` y el resto de archivos externos).
5. Hacé clic en **Build Wrapper** desde la raíz de la distribución (`target/gestion-1.0.0`). Al finalizar debe existir `target/gestion-1.0.0/BitsAndBytes.exe`.
6. Si preferís línea de comandos: ejecutá `launch4jc.exe installer\launch4j\gestion-launch4j.xml` desde `target/gestion-1.0.0`.

## 3. Empaquetar con Inno Setup

1. Instalá Inno Setup (incluye `ISCC.exe` para modo consola).
2. Asegurate de que `target/gestion-1.0.0/installer/assets/bits-and-bytes.ico` existe; Inno Setup lo usa para el icono del instalador y de los accesos directos.
3. Abrí `target/gestion-1.0.0/installer/inno/gestion.iss` y ajustá metadatos (nombre, versión, ícono) si lo necesitás. La carpeta de instalación predeterminada es `C:\\Program Files\\BitsAndBytes` (sin el `&`) para evitar que los scripts por lotes fallen: el carácter `&` es un separador de comandos en `cmd.exe` y rompía los accesos directos creados por el instalador.
4. Desde la GUI elegí **Compile** o bien ejecutá `"C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe" installer\inno\gestion.iss` desde `target/gestion-1.0.0`.
5. El instalador resultante `BitsAndBytesSetup.exe` queda en `target/`.

El script:
- Copia `gestion.exe`, la carpeta `app/`, las configuraciones (`config/`), la documentación y cualquier JRE que hayas colocado en `target/gestion-1.0.0/app/jre`.
- Crea la carpeta `logs/` en el directorio de instalación.
- Opcionalmente ejecuta `app/scripts/flyway-migrate.bat` al finalizar (el usuario lo selecciona en el wizard).
- Copia la documentación, incluido `docs/mysql_grant_gestion_system.sql` para asignar permisos a la base.
- Agrega accesos directos al menú inicio y, opcionalmente, al escritorio.
- Añade los accesos directos **"Abrir logs de errores"** y **"Diagnostico (consola)"** para facilitar el soporte.

## 4. Configuración de Flyway en tiempo de ejecución

La clase `configs.DatabaseMigration` ahora busca migraciones en los siguientes lugares:
1. `config/db/migration` dentro de la instalación.
2. Cualquier ruta adicional definida en la propiedad `flyway.locations` de `config/app.properties` o en variables del sistema.
3. `src/main/resources/db/migration` (modo desarrollo) y `classpath:db/migration` (migraciones empacadas).

Esto permite actualizar o añadir scripts después de instalar el sistema sin recompilar.

## 5. Pasos de verificación

1. Ejecutá `target/GestionSetup.exe` en una máquina limpia.
2. Editá `config/app.properties` con los datos reales de la base.
3. Ejecutá el acceso directo "Migraciones de base de datos" o marcá la tarea correspondiente durante la instalación para aplicar las migraciones.
4. Abrí la aplicación y comprobá el inicio de sesión, generación de logs y acceso a la base.

## 6. Automatizar todo con `build-installer.bat`

En `installer/windows/scripts/build-installer.bat` encontrarás un script que corre todo el proceso (Maven ➜ Launch4j ➜ Inno Setup).

1. Abrí una terminal de **Developer Command Prompt** o PowerShell con permisos de edición.
2. Definí las variables de entorno de herramientas (ajustá las rutas reales):
   - En **CMD**:
     ```bat
     set "LAUNCH4J_HOME=C:\Program Files\Launch4j"
     set "INNO_HOME=C:\Program Files (x86)\Inno Setup 6"
     ```
   - En **PowerShell** usá:
     ```powershell
     $env:LAUNCH4J_HOME = 'C:\Program Files\Launch4j'
     $env:INNO_HOME = 'C:\Program Files (x86)\Inno Setup 6'
     ```
   Si omitís las variables, el script intenta detectarlas automáticamente en las rutas de instalación típicas de 64 y 32 bits.
3. (Opcional) Si tu runtime no está en `installer/windows/runtime/`, definí la variable `BUNDLED_JRE_SOURCE` apuntando a la carpeta que contiene `bin\javaw.exe` (por ejemplo `set "BUNDLED_JRE_SOURCE=D:\JRE21"`).
4. Ejecutá el script desde la raíz del repositorio:
    ```bat
    installer\windows\scripts\build-installer.bat
    ```
5. El script compila la app, sincroniza el runtime embebido (si existe), genera `gestion.exe` y produce `target\GestionSetup.exe`. Antes de llamar a Launch4j copia la configuración `installer/launch4j/gestion-launch4j.xml` directamente a la raíz de la distribución (`target/gestion-1.0.0/gestion-launch4j-resuelto.xml`) y ejecuta `launch4jc.exe` desde allí. El XML deja fijado el classpath `app\gestion.jar` + `app\lib\*` y mantiene como carpeta de trabajo el directorio raíz de instalación, por lo que el `.exe` resultante levanta la app igual que el bat manual pero sin perder acceso a `config/`, `docs/` ni al runtime embebido en `app\jre`. Si algo falla corta con un mensaje de error.

Con estos archivos y scripts podés crear instaladores reproducibles usando Launch4j e Inno Setup, ya sea manualmente o con un solo comando.

## 7. Herramientas de diagnóstico incluidas en la instalación

El instalador copia las siguientes utilidades que ayudan a los usuarios finales a reportar problemas:

- `app/scripts/abrir-logs.bat`: abre automáticamente `%LOCALAPPDATA%\BitsAndBytes\logs\errores.log` (o la carpeta alternativa para el usuario actual) en el Bloc de notas y crea la carpeta si no existe.
- `app/scripts/diagnostico-app.bat`: ejecuta `Main --diagnostico` mostrando versión de Java y resultado de la conexión a MySQL.
- `docs/troubleshooting.md`: guía paso a paso (en español) para revisar Java, la configuración y las migraciones.

Ambos scripts quedan disponibles también como accesos directos en el menú inicio junto al resto de herramientas de Bits&Bytes.
