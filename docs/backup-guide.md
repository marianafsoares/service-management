# Backup workflow and Google Drive upload

## Local backup generation
- The application writes each database dump to the directory defined by `backup.output.dir` in `app.properties`. When `backup.client.name` is present the service automatically creates a subdirectory that matches a sanitized version of that value (for example, `Respaldos/LoleOliva`). With the default configuration, backups are still placed in the `backups/` folder that lives next to the application executable or the directory from which the JVM is launched.
- File names are composed from `backup.filename.prefix`, the client tag (when available), a suffix that identifies the backup strategy, the timestamp (`backup.timestamp.pattern`) and the `.sql.gz` suffix when compression is enabled. A sample path would be `Respaldos/LoleOliva/gestion-system-LoleOliva-full-20240508-233010.sql.gz`.
- Retention is controlled by `backup.retention.count`; when the number of backups exceeds this value, the oldest files are deleted locally before creating a new dump.
- Set `backup.notify.success` to `true` when you want the desktop application to display a confirmation dialog after every successful backup. The dialog now highlights both the exact file path and the folder (for example, `Respaldos/LoleOliva`) so operators know con precisión dónde quedan los respaldos locales antes de que el sistema termine de cerrarse.
- While the backup is running the UI shows a small "Realizando backup..." window to make it clear that the application is esperando la finalización del dump. The window now includes the absolute path of the local directory (for instance, `C:\Respaldos\LoleOliva`) so you can ubicar la carpeta incluso antes de que termine el proceso.

### MySQL client requirement
- The dump command relies on the `mysqldump` utility provided by the MySQL or MariaDB client tools. Install the client package on the machine running the application and make sure the binary is available on the `PATH`.
- When `db.pass` is set, the application automatically adds the corresponding `--password=...` flag to the command before executing it, even if the configured template omits the placeholder. This keeps older installations working without manual edits and still lets you provide a custom flag when necessary.
- Existing templates that relied on legacy forms such as `--password {password}` or `-p {password}` are rewritten on the fly to the safe `--password=...` format, so the dump command can authenticate correctly without prompting for input.
- On Windows the application now searches typical installation folders (for example, `C:\Program Files\MySQL\...` or `C:\Program Files\MariaDB\...`) when `mysqldump.exe` is not on the `PATH`, so the backup can run even if the client tools were installed without updating environment variables.
- If `mysqldump` lives in a non-standard location (for example, `"C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe"` on Windows), update `backup.command.full` and `backup.command.incremental` in `app.properties` to include the full path, e.g. `"C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe" --host=...`.
- When the executable cannot be found, the application will abort the backup and display a message so you can fix the installation or adjust the configured command before retrying.

## Google Drive replication
- After the local file is generated, the `backup.post.command` runs. The default command now wraps the local path in quotes and appends `{clientRemoteSuffix}` (which expands to either an empty string or `/<client>`), so rclone uploads the freshly created dump to the client-specific folder inside the Google Drive remote named `gdrive`.
- If the upload fails, the local backup remains in place. With the default configuration (`backup.post.failOnError=false`), the application keeps the dump and only records the issue in the log so the backup process can finish even when Google Drive is unavailable. Set the property to `true` if you prefer the UI to warn you about upload errors and give you the option to retry before closing the system.

## Configuring rclone when no browser code is returned
1. Install rclone from [https://rclone.org/downloads/](https://rclone.org/downloads/) on the server.
2. On a machine with a browser, run `rclone authorize "drive"`. Follow the login flow and, when the console shows `config_token = {...}`, copy everything that appears between the braces (the entire JSON block).
3. Back on the server, run `rclone config`, create the remote named `gdrive`, choose Google Drive, and when rclone asks for the verification code/token, paste the JSON you copied in the previous step. You should see the prompt continue immediately without needing to press any extra keys. Save the configuration.
4. Test the remote with `rclone ls gdrive:`. Once it works, the post-backup command will replicate every new dump to Google Drive automatically.

## Verifying the end-to-end flow
1. Start the application and trigger a backup by closing it (or use the manual control if available).
2. Confirm that a new file appears in the configured local directory (for instance, `Respaldos/LoleOliva`).
3. Run `rclone ls gdrive:gestion-system-backups/LoleOliva` (replace the client fragment accordingly) to verify the same file exists in Drive.
4. If any step fails, inspect the application logs for the `DatabaseBackupService` output and the rclone command result.

## Restoring a backup locally
- Locate the `.sql.gz` file you want to restore. Each file contains a full `mysqldump` of the database.
- Copy it to the server that hosts MySQL (or to a staging machine) and extract it:
  - **Linux/macOS**: `gunzip gestion-system-20240508-233010.sql.gz`
  - **Windows**: use a tool such as 7-Zip or run `gzip -d gestion-system-20240508-233010.sql.gz` from Git Bash/WSL to expand the archive.
- Load the resulting `.sql` file back into MySQL using the CLI:
  - `mysql --host=<host> --port=<port> --user=<user> --password=<pass> <database> < gestion-system-20240508-233010.sql`
- If you prefer to avoid creating an uncompressed file on disk, you can stream the restore directly:
  - `gunzip -c gestion-system-20240508-233010.sql.gz | mysql --host=<host> --port=<port> --user=<user> --password=<pass> <database>`
- Ensure the target database is empty (or that overwriting existing data is acceptable) before running the command.
