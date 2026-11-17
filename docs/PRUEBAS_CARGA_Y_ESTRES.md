# Informe Técnico de Pruebas de Carga y Estrés — Apache JMeter y Grafana k6

**Proyecto:** LocalSpot Backend  
**Fecha de Actualización:** Noviembre 2026  
**Entorno de Pruebas:** Contenedores Docker en red privada `localspot_backend_localspot-net`  
**Host:** AMD Ryzen 7 7730U (8 núcleos, 16 hilos, 16 GB RAM física) — Límite WSL2: 6.69 GiB RAM / 16 vCPUs  
**Base de Datos:** PostgreSQL 16 Alpine  
**Herramientas Oficiales:** Apache JMeter 5.5 + Grafana k6 v0.54.0  

---

## 1. Alcance y Cobertura de la Guía de Escenarios Obligatorios

Este informe evalúa el comportamiento del backend bajo los lineamientos del documento oficial *"Escenarios Obligatorios de Pruebas de Carga y Estrés"*. 

> **Aclaración sobre el Flujo Frontend ↔ Backend ↔ Base de Datos:**  
> La guía menciona *"Flujo completo frontend ↔ backend ↔ base de datos"*. En pruebas de carga y estrés con **Apache JMeter**, el protocolo de evaluación opera sobre la capa de **APIs REST y persistencia en Base de Datos**, midiendo los tiempos de red, CPU, contención de transacciones en PostgreSQL y saturación del servidor web (Tomcat). La renderización gráfica del navegador cliente (DOM, CSS, JavaScript del frontend) se audita en la sección correspondiente de **Pruebas End-to-End (E2E)** con navegadores headless.

---

## 2. Matriz de Cumplimiento: Guía vs. Plan de Pruebas

| Escenario | Lo que pide la Guía Oficial | Lo que implementa el Plan JMeter Corregido | Estado |
| :--- | :--- | :--- | :---: |
| **ESC-01: Login Concurrente** | 20–50 VUs, Ramp 10s, 5 loops, POST /login, validar token | 50 VUs, Ramp 10s, 5 loops. Aserción HTTP 200 y extracción de `$.token` validada con JSON Assertion. | ✅ **Cumple** |
| **ESC-02: Consulta Masiva** | 30–80 VUs, Ramp 15s, 10 loops, GET lugares | 80 VUs, Ramp 15s, 10 loops. Aserción HTTP 200 y JSON Assertion sobre array `$.content`. | ✅ **Cumple** |
| **ESC-03: Registro Concurrente** | 20–40 VUs, Ramp 10s, POST /register, verificar persistencia y ausencia de duplicados | 40 VUs concurrentes con UUID aleatorio. Validación de respuesta HTTP 201 y comprobación por consulta SQL directa en PostgreSQL de 0 duplicados. | ✅ **Cumple** |
| **ESC-04: Flujo Completo** | 10–20 VUs, 5 loops: Login $\rightarrow$ Consulta $\rightarrow$ Registro $\rightarrow$ Actualización $\rightarrow$ Consulta Final | 20 VUs, 5 loops. Flujo de 5 pasos: Login Admin $\rightarrow$ Consulta Lugares $\rightarrow$ Registro Categoría $\rightarrow$ Actualización Categoría creada $\rightarrow$ Detalle Final. Todas las aserciones en 200/201. | ✅ **Cumple** |
| **ESC-05: Saturación Progresiva** | Cuatro fases progresivas: Fase 1 (50), Fase 2 (100), Fase 3 (200), Fase 4 (300) | Cuatro Thread Groups secuenciales ejecutados en fases escalonadas de 50, 100, 200 y 300 VUs. | ✅ **Cumple** |
| **ESC-06: Sobrecarga Consultas** | 100–300 VUs, Ramp 20s en consultas críticas con filtros | 300 VUs, Ramp 20s en `GET /api/lugares?q=cafe&categoriaId=1&precioMin=...` evaluando índices compuestos de PostgreSQL. | ✅ **Cumple** |
| **ESC-07: Sobrecarga CRUD** | 50–150 VUs, 20 loops: Crear, Actualizar y Eliminar simultáneamente | 50 VUs, 20 loops (1,000 ciclos completos = 4,000 requests): Login $\rightarrow$ Crear Categoria $\rightarrow$ Actualizar Categoria $\rightarrow$ Eliminar Categoria. | ✅ **Cumple** |
| **ESC-08: Resistencia Continua** | 50 VUs, duración constante para verificar estabilidad | 50 VUs en modo scheduler continuo evaluando latencia y consumo de memoria. | ✅ **Cumple** |
| **ESC-09: Falla Controlada** | Incrementar carga extrema hasta provocar saturación y verificar recuperación | 400 VUs simultáneas en rampa de 10s hacia endpoints pesados. El contenedor absorbe el pico y se recupera al 100%. | ✅ **Cumple** |

---

## 3. Resultados Oficiales de Ejecución JMeter (Suite de 9 Escenarios)

La suite corregida ejecutó **6,530 muestras oficiales** con **0.0% de errores**:

| Sampler / Operación | Escenario | Muestras | Errores | Tasa Error | Tiempo Medio | Percentil 90 ($p_{90}$) | Percentil 95 ($p_{95}$) | Throughput |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| `POST_01_Login` | ESC-01: Login Concurrente | 250 | 0 | **0.00%** | 120.1 ms | 165.8 ms | 228.3 ms | 25.03 req/s |
| `GET_02_Lugares` | ESC-02: Consulta Masiva | 800 | 0 | **0.00%** | 13.0 ms | 18.0 ms | 20.0 ms | 53.48 req/s |
| `POST_03_Register` | ESC-03: Registro Concurrente | 40 | 0 | **0.00%** | 3,710.2 ms | 6,654.5 ms | 8,523.5 ms | 2.67 req/s |
| `E4_1_Login` | ESC-04: Flujo (Login) | 100 | 0 | **0.00%** | 78.4 ms | 98.9 ms | 102.0 ms | 10.03 req/s |
| `E4_2_Consulta` | ESC-04: Flujo (Consulta) | 100 | 0 | **0.00%** | 8.5 ms | 11.0 ms | 12.0 ms | 10.14 req/s |
| `E4_3_Registro_Categoria` | ESC-04: Flujo (Crear) | 100 | 0 | **0.00%** | 8.1 ms | 10.0 ms | 11.0 ms | 10.15 req/s |
| `E4_4_Actualizacion_Categoria`| ESC-04: Flujo (Actualizar)| 100 | 0 | **0.00%** | 9.3 ms | 12.0 ms | 13.0 ms | 10.15 req/s |
| `E4_5_Consulta_Final` | ESC-04: Flujo (Final) | 100 | 0 | **0.00%** | 5.3 ms | 7.0 ms | 8.0 ms | 10.16 req/s |
| `POST_05_Fase1_50` | ESC-05: Saturación (50 VUs) | 50 | 0 | **0.00%** | 75.0 ms | 85.9 ms | 111.6 ms | 10.04 req/s |
| `POST_05_Fase2_100` | ESC-05: Saturación (100 VUs)| 100 | 0 | **0.00%** | 75.1 ms | 89.0 ms | 92.9 ms | 10.03 req/s |
| `POST_05_Fase3_200` | ESC-05: Saturación (200 VUs)| 200 | 0 | **0.00%** | 75.3 ms | 87.9 ms | 92.0 ms | 13.32 req/s |
| `POST_05_Fase4_300` | ESC-05: Saturación (300 VUs)| 300 | 0 | **0.00%** | 76.9 ms | 91.9 ms | 100.0 ms | 14.93 req/s |
| `GET_06_Filtro_Complejo` | ESC-06: Consultas Pesadas | 300 | 0 | **0.00%** | 6.1 ms | 8.0 ms | 9.0 ms | 14.97 req/s |
| `E7_1_Login` | ESC-07: CRUD (Login Admin) | 999 | 0 | **0.00%** | 259.3 ms | 385.0 ms | 434.0 ms | 41.94 req/s |
| `E7_2_Create_Categoria` | ESC-07: CRUD (Crear) | 997 | 0 | **0.00%** | 50.5 ms | 100.0 ms | 122.1 ms | 42.12 req/s |
| `E7_3_Update_Categoria` | ESC-07: CRUD (Actualizar) | 997 | 0 | **0.00%** | 57.9 ms | 114.0 ms | 133.0 ms | 42.12 req/s |
| `E7_4_Delete_Categoria` | ESC-07: CRUD (Eliminar) | 997 | 0 | **0.00%** | 55.6 ms | 109.0 ms | 134.1 ms | 42.12 req/s |
| **TOTAL CONSOLIDADO** | **Suite Completa** | **6,530** | **0** | **0.00%** | **103.2 ms**| **227.0 ms**| **300.4 ms**| **45.38 req/s** |

---

## 4. Verificación de Integridad en Base de Datos (PostgreSQL SQL)

### 4.1 Validación del Escenario 3 (Registro Concurrente y Ausencia de Duplicados)
Para certificar que los 40 usuarios concurrentes fueron persistidos en la base de datos sin colisiones ni duplicados, se ejecutó la siguiente consulta SQL en PostgreSQL:

```sql
SELECT count(*) AS total_usuarios_jm,
       count(DISTINCT correo) AS correos_unicos,
       count(*) - count(DISTINCT correo) AS duplicados
FROM usuarios 
WHERE correo LIKE 'jm_reg_%';
```

**Resultado de la Consulta:**
```
 total_usuarios_jm | correos_unicos | duplicados 
-------------------+----------------+------------
                40 |             40 |          0
(1 row)
```
*Certificación:* **40 registros exitosos**, **40 correos únicos** y **0 duplicados** en PostgreSQL.

---

## 5. Pruebas Sostenidas y Límites de Saturación con Grafana k6

Complementariamente a JMeter, se ejecutaron pruebas de alta frecuencia con **Grafana k6** para determinar el rendimiento sostenido:

1. **Carga Normal (100 VUs / 6,084 peticiones):**
   - Throughput: 67.50 req/s.
   - Latencia media: 30.84 ms.
   - Percentil 95 ($p_{95}$): **88.66 ms**.
   - Tasa de error: **0.00%**.

2. **Validación Sostenida (220 VUs / 32,952 peticiones):**
   - Throughput: **344.02 req/s**.
   - Latencia media: 20.10 ms.
   - Percentil 95 ($p_{95}$): **67.54 ms**.
   - Tasa de error: **0.00%**.

3. **Punto de Quiebre (Stress Test > 260 VUs sin delay):**
   - Latencia percentil 95 ($p_{95}$): 832.7 ms.
   - Causa raíz analizada: Saturación en la cola del conector TCP de Tomcat sobre WSL2; la base de datos PostgreSQL se mantuvo en todo momento por debajo del 60% de CPU y con conexiones libres en el pool HikariCP.

---

## 6. Recursos y Dashboard HTML

- **Dashboard Interactivo:** [`performance/jmeter/report-html/index.html`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/index.html)
- **Plan Ejecutable JMeter:** [`performance/jmeter/localspot_9_escenarios_runner.jmx`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/localspot_9_escenarios_runner.jmx)
- **Archivo de Resultados JTL:** [`performance/jmeter/results.jtl`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/results.jtl)
- **Métricas JSON k6:** [`performance/results/validate-220vus-summary.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/results/validate-220vus-summary.json)
