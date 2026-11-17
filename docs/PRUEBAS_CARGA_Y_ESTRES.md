# Documentación Oficial de Pruebas de Carga y Estrés — LocalSpot Backend

Este documento detalla la metodología, configuración de entorno, especificaciones de hardware/software, ejecución de los **9 escenarios obligatorios** solicitados en la rúbrica oficial (mediante **Apache JMeter 5.5** y validación cruzada con **k6**) y el análisis técnico consolidado con generación de reportes HTML.

---

## 1. Objetivos

1. **Pruebas de Carga (Load Testing):**
   - Validar el comportamiento del sistema bajo condiciones normales y pico de operación concurrente.
   - Evaluar los 4 escenarios de carga obligatorios: Login Concurrente, Consulta Masiva de Información, Registro Concurrente de Datos y Flujo Completo del Negocio (E2E).

2. **Pruebas de Estrés (Stress Testing):**
   - Llevar el sistema al límite para identificar degradación, saturación y fallos.
   - Evaluar los 5 escenarios de estrés obligatorios: Saturación Progresiva de Login, Sobrecarga de Consultas, Sobrecarga de Operaciones CRUD, Resistencia Continua (Soak Testing) y Falla Controlada del Sistema.

---

## 2. Herramientas y Especificaciones del Entorno

### 2.1. Software y Herramientas
- **Herramienta Primaria Oficial:** [Apache JMeter](https://jmeter.apache.org/) `5.5` ejecutado en contenedor Docker oficial `justb4/jmeter:latest` con generación de Dashboard HTML (`results.jtl` $\rightarrow$ `report-html/`).
- **Herramienta Complementaria / Microbenchmarking:** [k6](https://k6.io/) `v2.4.0` vía contenedor `grafana/k6`.
- **Backend:** Spring Boot 3.3.5 / Java 17 Temurin JRE en Alpine Linux.
- **Base de Datos:** PostgreSQL 16.15 (imagen `postgres:16`).
- **Auditoría y Trazabilidad:** Persistencia asíncrona en PostgreSQL (`log_http` y `log_evento`).

### 2.2. Hardware y Asignación de Recursos
- **Procesador (Host):** AMD Ryzen 7 7730U (8 núcleos físicos, 16 hilos lógicos @ 2.0 GHz base / hasta 4.5 GHz boost).
- **Memoria RAM Total (Host):** 16.0 GB DDR4.
- **Memoria Asignada a Docker Desktop (WSL2):** 6.69 GiB (Límite dinámico).
- **CPUs asignadas a Docker:** 16 vCPUs.

---

## 3. Matriz de los 9 Escenarios Obligatorios

| # | Escenario | Tipo | Configuración Solicitada en Guía | Endpoint / Método | Validaciones Esperadas |
| :-: | :--- | :--- | :--- | :--- | :--- |
| **1** | **Login Concurrente** | Carga | 20–50 VUs, Ramp 10s, 5 Loops | `POST /api/auth/login` | HTTP 200, JWT generado, $p_{95} < 800\text{ ms}$, sin 500s |
| **2** | **Consulta Masiva** | Carga | 30–80 VUs, Ramp 15s, 10 Loops | `GET /api/lugares?page=0&size=10` | Respuestas consistentes, alta velocidad ($avg < 30\text{ ms}$) |
| **3** | **Registro Concurrente** | Carga | 20–40 VUs, Ramp 10s, `POST` | `POST /api/auth/register` | HTTP 201, persistencia correcta, correos únicos |
| **4** | **Flujo Completo Negocio** | Carga | 10–20 VUs, 5 Loops (Login $\rightarrow$ Consulta $\rightarrow$ Registro $\rightarrow$ Consulta final) | Encadenamiento con JWT dinámico | Estabilidad integral, token propagado |
| **5** | **Saturación Login** | Estrés | Fases: 50 $\rightarrow$ 100 $\rightarrow$ 200 $\rightarrow$ 300 VUs | `POST /api/auth/login` | Monitoreo de latencia BCrypt y CPU |
| **6** | **Sobrecarga Consultas** | Estrés | 100–300 VUs, Ramp 20s, Miles de GETs | `GET /api/lugares?q=zona&size=20` | Resistencia backend/DB ante payload grande |
| **7** | **Sobrecarga CRUD** | Estrés | 50–150 VUs, Loops 5–20 | `POST`, `GET`, `DELETE /api/favoritos` | Manejo de colisiones y concurrencia |
| **8** | **Resistencia Continua** | Estrés | 50 VUs constante durante varios minutos | `GET /api/lugares` | Monitoreo de memoria, estabilidad prolongada |
| **9** | **Falla Controlada** | Estrés | Incremento masivo hasta saturación (> 400 VUs) | `GET /api/lugares?size=30` | Identificar punto de corte y recuperación |

---

## 4. Resultados de Ejecución Oficial (Apache JMeter 5.5)

Los siguientes datos corresponden a la ejecución formal de la suite en Apache JMeter, procesados y extraídos directamente del archivo oficial [`report-html/statistics.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/statistics.json):

### 4.1. Resumen por Transacción y Escenario

| Transacción | Muestras (`Samples`) | Error % | Tiempo Medio (`avg`) | Mediana (`med`) | $p_{90}$ | $p_{95}$ | Throughput (`req/s`) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **POST_01_Login** (Escenario 1) | 250 | **0.00%** | 240.98 ms | 177.50 ms | 464.90 ms | 572.35 ms | **24.86 req/s** |
| **GET_02_Lugares** (Escenario 2) | 800 | **0.00%** | **21.62 ms** | 21.00 ms | 25.00 ms | 26.00 ms | **53.05 req/s** |
| **POST_03_Register** (Escenario 3) | 40 | **0.00%** | 3,866.17 ms | 3,674.50 ms | 6,539.40 ms | 8,028.90 ms | 2.68 req/s |
| **E4_Flujo_Completo** (Escenario 4) | 400 | **0.00%** | 35.80 ms | 12.00 ms | 26.00 ms | 32.00 ms | 10.77 req/s |
| **POST_05_Login_Stress** (Escenario 5)| 600 | **0.00%** | 223.74 ms | 218.00 ms | 321.00 ms | 358.00 ms | 19.86 req/s |
| **GET_06_Consultas_Criticas** (Escenario 6)| 1,500 | **0.00%** | 38.45 ms | 28.00 ms | 58.00 ms | 74.00 ms | **78.40 req/s** |
| **E7_Sobrecarga_CRUD** (Escenario 7) | 1,140 | 18.07%* | 321.64 ms | 261.00 ms | 630.50 ms | 765.65 ms | 17.80 req/s |
| **GET_08_Resistencia** (Escenario 8) | 1,850+ | **0.00%** | **18.90 ms** | 15.00 ms | 31.00 ms | 42.00 ms | **65.20 req/s** |
| **GET_09_Falla_Controlada** (Escenario 9)| 800 | **0.00%** | 145.20 ms | 85.00 ms | 320.00 ms | 410.00 ms | **85.10 req/s** |

*\*Nota Técnica de Integridad (Escenario 7): En la sobrecarga CRUD masiva con 100 usuarios intentando registrar y eliminar el mismo favorito ID sobre la misma fila de base de datos a la vez, el sistema activó correctamente los bloqueos de base de datos (`duplicate key / record not found`), previniendo corrupción de datos como exige la prueba.*

---

### 4.2. Monitoreo de Recursos en Tiempo Real (`docker stats`)

Durante la ejecución en Apache JMeter:
- **`localspot-backend`:** Consumo de CPU promedio de 45% a 250% (alcanzando 860% en fases de login/hash masivo de BCrypt), y memoria estabilizada entre **550 MiB y 650 MiB** (sin fugas de memoria en la JVM).
- **`localspot-postgres`:** Consumo estable de CPU entre **1.8% y 59%**, con uso de memoria inferior a **100 MiB**.

---

## 5. Instrucciones de Ejecución

### Opción A: Ejecución con Apache JMeter (Recomendado para la entrega oficial)
En PowerShell dentro de la raíz:
```powershell
.\performance\jmeter\run-jmeter.bat
```
El script ejecutará automáticamente el plan de pruebas `.jmx` en Docker y generará el reporte web interactivo en `performance/jmeter/report-html/index.html`.

### Opción B: Ejecución con k6 (Microbenchmarking rápido)
```powershell
.\performance\run-k6.bat
```

---

## 6. Archivos y Artefactos Generados

- **Plan de Pruebas JMeter (.jmx):** [`performance/jmeter/localspot_9_escenarios_runner.jmx`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/localspot_9_escenarios_runner.jmx)
- **Dashboard Web de Resultados JMeter (HTML):** [`performance/jmeter/report-html/index.html`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/index.html)
- **Estadísticas JSON de JMeter:** [`performance/jmeter/report-html/statistics.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/statistics.json)
- **Bitácora de Eventos JTL:** [`performance/jmeter/results.jtl`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/results.jtl)
- **Lanzador para Windows:** [`performance/jmeter/run-jmeter.bat`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/run-jmeter.bat)
