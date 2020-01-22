@ECHO OFF

REM Easily pass arguments to the jar with maven as if it were any other program.
REM 'You gon die' -- A Java developer

if "%MAVEN_OPTS%" == "" (
    set MAVEN_OPTS=--enable-preview
)

mvn exec:java -Dexec.mainClass="org.m_flak.myblog.server.App" -Dexec.args="%*"
