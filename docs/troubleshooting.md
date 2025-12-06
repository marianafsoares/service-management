# Guia de diagnostico para Bits&Bytes

Esta guia resume los pasos recomendados cuando la aplicacion no inicia o muestra errores de conexion con MySQL.

## 1. Revisar los logs

1. Abrir el acceso directo **"Abrir logs de errores"** creado en el menu inicio durante la instalacion. Tambien podes ejecutar `app\scripts\abrir-logs.bat` desde la carpeta de instalacion (por defecto `C:\\Program Files\\BitsAndBytes`).
2. Se abrira `%LOCALAPPDATA%\BitsAndBytes\logs\errores.log` (o la ruta equivalente en tu usuario). Verificá los mensajes recientes (al final del archivo) para identificar la causa exacta.
3. Si el archivo no existe, iniciá la aplicacion una vez o ejecutá el diagnostico (ver seccion siguiente) para generarlo.

## 2. Ejecutar el diagnostico en consola

1. Usá el acceso directo **"Diagnostico (consola)"** o ejecutá `app\scripts\diagnostico-app.bat` manualmente.
   - Si `cmd.exe` indica `El sistema no puede encontrar la ruta especificada`, verificá primero el nombre real de la carpeta con `dir /ad /b "C:\\Program Files" ^| findstr /I Bits` y copiá el nombre tal cual aparezca en el listado. Algunos entornos corporativos renombran la carpeta durante la instalación y cualquier diferencia hará que las rutas relativas calculadas por el script fallen (`set "INSTALL_DIR=%~dp0.."`).
   - Cuando ejecutes el `.bat` directamente desde la consola, envolvé siempre la ruta entre comillas (`"C:\\Program Files\\BitsAndBytes\\app\\scripts\\diagnostico-app.bat"`). Sin las comillas `cmd.exe` corta la instrucción en `C:\\Program`, provocando el mensaje `'C:\\Program' no se reconoce como un comando interno o externo`.
   - Los accesos directos generados por el instalador ya configuran el directorio de inicio en `app\\scripts` para evitar este problema (`WorkingDir: "{app}\\app\\scripts"`). Si editaste los accesos directos a mano, restablecé ese valor.【F:installer/windows/scripts/diagnostico-app.bat†L4-L6】【F:installer/windows/inno/gestion.iss†L45-L50】
2. El script muestra: version de Java detectada, ubicacion de la instalacion y resultado de la conexion a la base de datos.
3. Si la conexion falla, el script imprime el `SQLState` y el codigo de error que reporta MySQL. Compartí estos datos para acelerar el soporte.

## 3. Verificar la instalacion de Java

- El instalador puede incluir un JRE en la carpeta `jre`. Si esa carpeta no existe, asegurate de tener Java 21 instalado y disponible en el `PATH` ejecutando `java -version` en una consola.
- Si ya instalaste Java en `C:\Program Files\Java\jdk-21` (u otra carpeta similar) pero el diagnostico informa que no encuentra Java, definí la variable de entorno `JAVA_HOME` apuntando a esa carpeta y agregá `%JAVA_HOME%\bin` al `PATH`.
- El acceso directo **"Diagnostico (consola)"** ahora intenta detectar Java automaticamente en `JAVA_HOME`, `C:\Program Files\Java` y en el `PATH`. Si encuentra una version anterior a la 21 la ignora e imprime un aviso indicando la version detectada para que la actualices.
- Si ninguna ruta contiene Java 21, instala [Temurin 21](https://adoptium.net/) o actualizá las variables de entorno antes de volver a ejecutar el script.

## 4. Confirmar la configuracion de la base de datos

1. Editá `config/app.properties` y validá los valores `db.url`, `db.user` y `db.pass`.
2. Si el servidor requiere `allowPublicKeyRetrieval=true`, no hace falta agregarlo manualmente: la aplicacion lo agrega automaticamente.
3. Asegurate de que el usuario MySQL tenga permisos de `CREATE DATABASE`, `ALTER`, `INSERT`, `UPDATE` y `DELETE` sobre el esquema configurado. El script `docs/mysql_grant_gestion_system.sql` contiene un ejemplo de permisos.

## 5. Probar las migraciones manualmente

- Ejecutá `app\scripts\flyway-migrate.bat` para aplicar las migraciones con el mismo classpath que usa la aplicacion.
- Si falla, revisá el mensaje completo y comparalo con el log `%LOCALAPPDATA%\BitsAndBytes\logs\errores.log` (o la carpeta alternativa indicada por el script).

## 6. Ubicacion de archivos utiles

- `%LOCALAPPDATA%\BitsAndBytes\logs\errores.log`: detalle completo de excepciones.
- `docs/troubleshooting.md`: esta guia de diagnostico.
- `config/app.properties`: configuracion de la base y ubicaciones adicionales de migraciones.
- `installer/windows/scripts/diagnostico-app.bat`: script original incluido en la instalacion.

Con estos pasos podras recopilar la informacion necesaria para reportar el problema y validar rapidamente si se trata de una configuracion de entorno o de la base de datos.

## 7. Errores comunes al construir el instalador

- **Launch4j: Application jar doesn't exist.** Asegurate de que `target/gestion-1.0.0/app/gestion.jar` exista antes de ejecutar `build-installer.bat`. El script genera una copia temporal de `gestion-launch4j.xml` con rutas absolutas; si falla la generación verás `[ERROR] No se pudo generar la configuracion temporal para Launch4j`. En ese caso revisá los permisos de PowerShell o ejecutá nuevamente `mvn clean package` para regenerar la distribución.
- **Join-Path : Cannot find drive 'C:'** o similares. La versión actual del script genera las rutas absolutas con `System.IO.Path::Combine`, por lo que este error ya no debería aparecer. Si lo ves, asegurate de haber actualizado el repositorio y volvé a ejecutar `installer\windows\scripts\build-installer.bat`.

## 8. Error "Type interface mappers.ProductMapper is not known to the MapperRegistry"

- La aplicación registra automáticamente todos los mappers compilados dentro de `app\gestion.jar` y luego carga los XML opcionales que encuentres en `config\mappers`. Si el error aparece al abrir una pantalla, revisá primero `%LOCALAPPDATA%\BitsAndBytes\logs\errores.log` para confirmar cuántos mappers se registraron (se informa con el mensaje `Mappers MyBatis registrados: N`).
- Si el registro devuelve `0`, verificá que `app\gestion.jar` esté presente y que la carpeta `app\lib` no falte en la instalación. Reinstalá generando nuevamente el instalador (`installer\windows\scripts\build-installer.bat`) para reconstruir ambas carpetas.
- Cuando el log muestre un número distinto de cero pero falten operaciones específicas, asegurate de que los archivos XML correspondientes existan (ya sea dentro del JAR o como overrides en `config\mappers`). Copiá nuevamente los XML desde el repositorio si fuese necesario.

## 9. Error "Error generando el backup" al escribir en `C:\Program Files\BitsAndBytes\backups`

- Windows suele requerir permisos de administrador para crear carpetas dentro de `C:\Program Files`. Si la aplicación no puede crear `backups`, ahora cambia automáticamente a `%LOCALAPPDATA%\BitsAndBytes\backups` (o a tu carpeta de usuario si tampoco existe `LOCALAPPDATA`).
- Verificá el log para identificar la ruta efectiva y asegurate de que haya espacio libre en ese directorio. Si preferís otra carpeta, editá `backup.output.dir` en `config/app.properties` apuntando a una ubicación donde tengas permisos de escritura.
