import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const failedRequests = new Rate('failed_requests');
const responseTimeTrend = new Trend('custom_req_duration');

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:18080';

export const options = {
  stages: [
    { duration: '30s', target: 50 },   // Escalado inicial rápido
    { duration: '1m',  target: 150 },  // Carga pesada
    { duration: '1m',  target: 300 },  // Punto de estrés alto
    { duration: '30s', target: 450 },  // Punto de quiebre / saturación
    { duration: '30s', target: 0 },    // Enfriamiento y recuperación
  ],
  thresholds: {
    // En prueba de estrés se evalúa hasta dónde aguanta el sistema
    http_req_duration: ['p(95)<2500'], // Tolerar hasta 2.5s en estrés máximo
    failed_requests: ['rate<0.05'],    // Menos de 5% de errores globales
  },
};

export default function () {
  // Petición combinada: catálogo general y filtrado concurrente
  const resLugares = http.get(`${BASE_URL}/api/lugares?page=0&size=20`, {
    headers: { 'Accept': 'application/json' },
  });

  const checkOk = check(resLugares, {
    'GET /api/lugares status is 200': (r) => r.status === 200,
  });

  failedRequests.add(!checkOk);
  responseTimeTrend.add(resLugares.timings.duration);

  // Menor tiempo de espera (sleep) para intensificar la presión
  sleep(0.3);

  // Búsqueda por palabra clave
  const resSearch = http.get(`${BASE_URL}/api/lugares?q=zona&page=0&size=10`, {
    headers: { 'Accept': 'application/json' },
  });

  const checkSearch = check(resSearch, {
    'GET /api/lugares?q=zona status is 200': (r) => r.status === 200,
  });

  failedRequests.add(!checkSearch);
  responseTimeTrend.add(resSearch.timings.duration);

  sleep(0.3);
}
