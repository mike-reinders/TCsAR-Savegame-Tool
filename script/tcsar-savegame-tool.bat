@echo off

echo.
echo.

set interactive=1
echo %cmdcmdline% | find /i "%~0" >nul
if not errorlevel 1 set interactive=0

if not exist tcsar-savegame-tool.jar (
    echo tcsar-savegame-tool.jar not found.
) else (
    java -version >NUL 2>&1
    if not errorlevel 0 (
        echo Java seems to be not properly installed. The JAVA_HOME environment variable might not be setup correctly.
    )

    java -jar tcsar-savegame-tool.jar %*
)

echo.

if _%1_==__ (
    echo usage: %~nx0 filename
    if _%interactive%_==_0_ pause
)