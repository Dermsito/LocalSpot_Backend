@echo off
REM ========================================================
REM Script para ejecutar pruebas de Carga y Estrés con k6 en Docker
REM LocalSpot Backend
REM ========================================================

set SCRIPT_DIR=%~dp0
set NETWORK_NAME=localspot_backend_localspot-net

echo ========================================================
echo Ejecutando Pruebas de Rendimiento en LocalSpot
echo Red Docker: %NETWORK_NAME%
echo Endpoint Destino: http://localspot-backend:8080
echo ========================================================
echo 1. Ejecutar Prueba de Carga (Load Test - 100 VUs, ~2.5 min)
echo 2. Ejecutar Prueba de Estres (Stress Test - 450 VUs, ~3.5 min)
echo 3. Ejecutar Ambas
echo ========================================================

set /p OPTION="Seleccione una opcion [1-3]: "

if "%OPTION%"=="1" (
    docker run --rm -i --network %NETWORK_NAME% -v "%SCRIPT_DIR%k6:/scripts" -e BASE_URL=http://localspot-backend:8080 grafana/k6 run /scripts/load-test.js
) else if "%OPTION%"=="2" (
    docker run --rm -i --network %NETWORK_NAME% -v "%SCRIPT_DIR%k6:/scripts" -e BASE_URL=http://localspot-backend:8080 grafana/k6 run /scripts/stress-test.js
) else if "%OPTION%"=="3" (
    echo [1/2] Iniciando Prueba de Carga...
    docker run --rm -i --network %NETWORK_NAME% -v "%SCRIPT_DIR%k6:/scripts" -e BASE_URL=http://localspot-backend:8080 grafana/k6 run /scripts/load-test.js
    echo [2/2] Iniciando Prueba de Estres...
    docker run --rm -i --network %NETWORK_NAME% -v "%SCRIPT_DIR%k6:/scripts" -e BASE_URL=http://localspot-backend:8080 grafana/k6 run /scripts/stress-test.js
) else (
    echo Opcion invalida.
)

pause

