@echo off
setlocal
set BASEDIR=%~dp0
set VERSION=3.9.12
set MAVEN_HOME=%BASEDIR%.mvn\wrapper\apache-maven-%VERSION%
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  if not exist "%BASEDIR%.mvn\wrapper" mkdir "%BASEDIR%.mvn\wrapper"
  powershell -NoProfile -Command "Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%VERSION%/apache-maven-%VERSION%-bin.zip' -OutFile '%BASEDIR%.mvn\wrapper\apache-maven-%VERSION%-bin.zip'; Expand-Archive -Force '%BASEDIR%.mvn\wrapper\apache-maven-%VERSION%-bin.zip' '%BASEDIR%.mvn\wrapper'"
)
call "%MAVEN_HOME%\bin\mvn.cmd" %*
