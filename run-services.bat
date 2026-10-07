@echo off
setlocal

cd /d "%~dp0"

echo Building all services...
call mvnw.cmd clean verify
if errorlevel 1 (
    echo Build failed. Services were not started.
    exit /b %errorlevel%
)

echo Starting catalog-service on port 8081...
start "catalog-service" /D "%~dp0" cmd /k "call mvnw.cmd -pl catalog-service spring-boot:run"

echo Starting pricing-service on port 8082...
start "pricing-service" /D "%~dp0" cmd /k "call mvnw.cmd -pl pricing-service spring-boot:run"

echo Starting availability-service on port 8083...
start "availability-service" /D "%~dp0" cmd /k "call mvnw.cmd -pl availability-service spring-boot:run"

echo Starting customer-service on port 8084...
start "customer-service" /D "%~dp0" cmd /k "call mvnw.cmd -pl customer-service spring-boot:run"

echo Starting aggregation-service on port 8080...
start "aggregation-service" /D "%~dp0" cmd /k "call mvnw.cmd -pl aggregation-service spring-boot:run"

echo.
echo All services are starting in separate windows.
endlocal
