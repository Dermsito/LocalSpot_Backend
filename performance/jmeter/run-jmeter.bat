@echo off
REM ========================================================
REM Script para ejecutar Suite JMeter (9 Escenarios Obligatorios)
REM LocalSpot Backend
REM ========================================================

set SCRIPT_DIR=%~dp0
set NETWORK_NAME=localspot_backend_localspot-net

echo ========================================================
echo Ejecutando Suite Oficial Apache JMeter en LocalSpot
echo Red Docker: %NETWORK_NAME%
echo Endpoint Destino: http://localspot-backend:8080
echo ========================================================

if exist "%SCRIPT_DIR%report-html" (
    rmdir /s /q "%SCRIPT_DIR%report-html"
)
if exist "%SCRIPT_DIR%results.jtl" (
    del /f /q "%SCRIPT_DIR%results.jtl"
)

docker run --rm -i --network %NETWORK_NAME% -v "%SCRIPT_DIR%:/jmeter" justb4/jmeter -n -t /jmeter/localspot_9_escenarios_runner.jmx -l /jmeter/results.jtl -e -o /jmeter/report-html

echo ========================================================
echo Reporte HTML generado en performance\jmeter\report-html\index.html
echo ========================================================
pause
