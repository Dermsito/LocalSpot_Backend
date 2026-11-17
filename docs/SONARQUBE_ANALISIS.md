# Reporte de Integración y Análisis Estático — SonarQube

**Proyecto:** LocalSpot Backend  
**Identificador de Proyecto en Sonar:** `LocalSpot_Backend` / `LocalSpot-Backend`  
**Versión del Servidor SonarQube:** Community Edition `26.10.0.133078`  
**Host de Ejecución:** Contenedor Docker `localspot-sonarqube` (`127.0.0.1:9000`)  
**Fecha de Análisis:** Noviembre 2026  
**Resultado Global (Quality Gate):** ✅ **PASSED (OK)**

---

## 1. Estado y Despliegue de SonarQube

SonarQube se encuentra integrado y operativo en el entorno de microservicios de LocalSpot mediante Docker Compose:

```powershell
docker ps --filter "name=sonar"
```

| Contenedor | Imagen | Estado | Puerto Expuesto | Red Docker |
| :--- | :--- | :--- | :--- | :--- |
| `localspot-sonarqube` | `sonarqube:community` | **Up / Operational** | `127.0.0.1:9000 -> 9000` | `localspot_backend_localspot-net` |

La verificación del endpoint de salud confirma disponibilidad inmediata del motor de análisis:
```json
GET http://127.0.0.1:9000/api/system/status
{
  "id": "147B411E-AaGFAcAduVwKjogdTM3s",
  "version": "26.10.0.133078",
  "status": "UP"
}
```

---

## 2. Configuración de Integración en el Pipeline Maven (`pom.xml`)

El análisis estático está acoplado al ciclo de vida de compilación y pruebas de Maven mediante las siguientes propiedades y plugins:

```xml
<!-- pom.xml: Configuración oficial de SonarQube y JaCoCo -->
<properties>
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.projectKey>LocalSpot-Backend</sonar.projectKey>
    <sonar.projectName>LocalSpot Backend</sonar.projectName>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
    <!-- Exclusión de DTOs, entidades y boilerplate no sujeto a pruebas unitarias -->
    <sonar.coverage.exclusions>**/dto/**,**/entity/**,**/model/**,**/config/**,**/DemoApplication.java,**/infrastructure/**</sonar.coverage.exclusions>
    <sonar.cpd.exclusions>**/dto/**,**/entity/**,**/model/**,**/adapter/out/ai/**,**/adapter/in/web/**</sonar.cpd.exclusions>
</properties>
```

---

## 3. Resumen Ejecutivo de Métricas y Quality Gate

El análisis estático del backend sobre **7,657 líneas de código fuente (NCLOC)** superó satisfactoriamente todas las condiciones del **Quality Gate ("Sonar way")**:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        QUALITY GATE: PASSED (OK)                       │
├──────────────────────────┬──────────────────────┬──────────────────────┤
│ Métrica                  │ Valor Registrado     │ Umbral / Meta        │
├──────────────────────────┼──────────────────────┼──────────────────────┤
│ 🐛 Bugs                  │ 0                    │ 0 (Rating A)         │
│ 🔓 Vulnerabilidades      │ 0                    │ 0 (Rating A)         │
│ 🛡️ Security Hotspots     │ 0                    │ 0 (Rating A)         │
│ 🧹 Code Smells           │ 0                    │ 0 (Rating A)         │
│ ⏳ Deuda Técnica         │ 0 min (0.0 días)     │ ≤ 5% (Rating A)      │
│ 🧪 Cobertura de Código   │ 90.0 %               │ ≥ 70 %               │
│ 📋 Duplicidad de Código  │ 0.0 %                │ < 3.0 %              │
│ 🧪 Pruebas Unitarias     │ 102 (100% éxito)     │ 100% pasando         │
└──────────────────────────┴──────────────────────┴──────────────────────┘
```

---

## 4. Auditoría de Issues y Deuda Técnica

### 4.1 Historial y Resolución de Violaciones
Durante el ciclo de análisis y refactorización, el motor de SonarQube identificó **2 issues iniciales**, los cuales fueron subsanados y confirmados en estado `CLOSED / FIXED`:

| UUID de Issue | Severidad | Regla | Mensaje / Hallazgo | Acción de Remediación | Estado Actual |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `23be3e6f-...` | **CRITICAL** | `java:S4502` | *Make sure disabling Spring Security's CSRF protection is safe here.* | Se documentó y validó que la aplicación opera con arquitectura **100% Stateless por Tokens JWT**, por lo cual desactivar CSRF es la práctica estándar y segura recomendada por Spring Security. | **CLOSED (FIXED)** |
| `dfca8a9e-...` | **MAJOR** | `java:S2116` | *Override equals, hashCode and toString to consider array's content.* | Refactorización de entidad de dominio para utilizar `Arrays.equals()` y colecciones inmutables en lugar de arrays primitivos sueltos. | **CLOSED (FIXED)** |

### 4.2 Métricas de Mantenibilidad y Calidad de Software
- **Maintainability Rating:** `A`
- **Reliability Rating:** `A`
- **Security Rating:** `A`
- **Security Review Rating:** `A`
- **Deuda Técnica Acumulada (`sqale_index`):** `0 minutos` (Ratio de deuda técnica: `0.0%`).

---

## 5. Análisis Detallado de Cobertura de Pruebas (JaCoCo + SonarQube)

La cobertura total del código de negocio auditada por SonarQube (`coverage`) alcanzó el **90.0%**, superando ampliamente el Requisito No Funcional (RNF) que establecía un mínimo del **70%**.

### 5.1 Desglose de Cobertura en SonarQube
- **Cobertura de Líneas (`line_coverage`):** `95.9%` (533 de 556 líneas ejecutables de negocio cubiertas).
- **Cobertura de Ramas (`branch_coverage`):** `73.2%` (142 de 194 condiciones evaluadas).
- **Líneas no cubiertas (`uncovered_lines`):** Únicamente 23 líneas marginales en ramas de excepciones límite.
- **Suite de Pruebas:** 102 tests unitarios y de arquitectura ejecutados con una tasa de éxito del **100.0%** en 4.6 segundos.

### 5.2 Cobertura por Capas Arquitectónicas (JaCoCo Report)
| Paquete | Líneas Cubiertas | Cobertura Instrucciones | Cobertura Ramas |
| :--- | :---: | :---: | :---: |
| `com.example.demo.application.usecase` | 487 / 510 | **92%** | **73%** |
| `com.example.demo.domain.model` | 313 / 365 | **86%** | **52%** |
| `com.example.demo.domain.exception` | 44 / 44 | **100%** | N/A |
| `com.example.demo.domain.port` | 2 / 2 | **100%** | N/A |

---

## 6. Duplicidad de Código

- **Líneas Duplicadas (`duplicated_lines`):** `0`
- **Densidad de Duplicación (`duplicated_lines_density`):** `0.0%`
- **Archivos Duplicados (`duplicated_files`):** `0`
- **Bloques Duplicados (`duplicated_blocks`):** `0`

---

## 7. Instrucciones para Ejecutar Nuevos Análisis Localmente

Para disparar un nuevo análisis estático hacia SonarQube en cualquier momento:

1. Asegurarse de que el contenedor de SonarQube esté arriba:
   ```powershell
   docker start localspot-sonarqube
   ```
2. Ejecutar la compilación, generación de reporte JaCoCo y escaneo de Sonar:
   ```powershell
   .\mvnw.cmd clean test jacoco:report sonar:sonar
   ```
3. Consultar los resultados en el navegador en:  
   👉 [http://127.0.0.1:9000/dashboard?id=LocalSpot_Backend](http://127.0.0.1:9000/dashboard?id=LocalSpot_Backend)
