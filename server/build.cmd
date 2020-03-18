@ECHO OFF

REM Build project. Create keystore if not found

cmd /Q /C "dir /B src\main\resources\keystore.jks > NUL 2>&1"
if %ERRORLEVEL% equ 1 mvn keytool:generateKeyPair && mvn package
mvn package
