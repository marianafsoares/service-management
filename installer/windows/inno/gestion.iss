#define AppName "BitsAndBytes"
#define AppDirName "BitsAndBytes"
#define AppVersion "1.0.0"
#define AppExe "BitsAndBytes.exe"

[Setup]
AppId={{2F7E7C4E-9C68-4D2E-9E08-0BBEEA1B7C62}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher=BYB Sistemas
AppPublisherURL=https://github.com/
DefaultDirName={pf64}\{#AppDirName}
DefaultGroupName={#AppName}
DisableDirPage=no
DisableProgramGroupPage=no
OutputDir=..\..\..
OutputBaseFilename=BitsAndBytesSetup
Compression=lzma2
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64
UninstallDisplayIcon={app}\{#AppExe}
SetupLogging=yes
WizardStyle=modern
SetupIconFile=..\assets\bits-and-bytes.ico

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "Crear acceso directo en el escritorio"; GroupDescription: "Accesos directos:"; Flags: unchecked
Name: "runmigrations"; Description: "Ejecutar migraciones de la base de datos al finalizar"; Flags: unchecked

[Dirs]
Name: "{app}\logs"

[Files]
; EXE generado por Launch4j
Source: "..\..\BitsAndBytes.exe"; DestDir: "{app}"; Flags: ignoreversion

; App completa (lib, scripts, jars, etc.)
Source: "..\..\app\*"; DestDir: "{app}\app"; Flags: ignoreversion recursesubdirs createallsubdirs

; Config externa, docs e instalador
Source: "..\..\config\*"; DestDir: "{app}\config"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\..\docs\*"; DestDir: "{app}\docs"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\..\installer\*"; DestDir: "{app}\installer"; Flags: ignoreversion recursesubdirs createallsubdirs

; run-app.bat está en installer\windows\scripts en el proyecto
Source: "..\..\..\..\installer\windows\scripts\run-app.bat"; \
    DestDir: "{app}\app\scripts"; Flags: ignoreversion

[Icons]
; Acceso directo principal: usa run-app.bat
Name: "{group}\{#AppName}"; \
    Filename: "{app}\app\scripts\run-app.bat"; \
    WorkingDir: "{app}\app"; \
    IconFilename: "{app}\installer\assets\bits-and-bytes.ico"

Name: "{group}\Migraciones de base de datos"; \
    Filename: "{app}\app\scripts\flyway-migrate.bat"; \
    WorkingDir: "{app}\app\scripts"; \
    IconFilename: "{sys}\cmd.exe"; \
    Flags: runminimized

Name: "{group}\Abrir logs de errores"; \
    Filename: "{app}\app\scripts\abrir-logs.bat"; \
    WorkingDir: "{app}\app\scripts"; \
    IconFilename: "{sys}\notepad.exe"; \
    Flags: runminimized

Name: "{group}\Diagnostico (consola)"; \
    Filename: "{app}\app\scripts\diagnostico-app.bat"; \
    WorkingDir: "{app}\app\scripts"; \
    IconFilename: "{sys}\cmd.exe"

; Icono del escritorio
Name: "{userdesktop}\{#AppName}"; \
    Filename: "{app}\app\scripts\run-app.bat"; \
    WorkingDir: "{app}\app"; \
    IconFilename: "{app}\installer\assets\bits-and-bytes.ico"; \
    Tasks: desktopicon

[Run]
; Lanzar la app al terminar la instalación
Filename: "{app}\app\scripts\run-app.bat"; \
    Description: "{cm:LaunchProgram, {#AppName}}"; \
    WorkingDir: "{app}\app"; \
    Flags: nowait postinstall skipifsilent

; Migraciones opcionales
Filename: "{app}\app\scripts\flyway-migrate.bat"; \
    Description: "Ejecutar migraciones de base de datos"; \
    WorkingDir: "{app}\app\scripts"; \
    Flags: postinstall waituntilterminated runminimized; \
    Tasks: runmigrations

[UninstallDelete]
Type: filesandordirs; Name: "{app}\logs"
