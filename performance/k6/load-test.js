import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Métricas personalizadas
const failedRequests = new Rate('failed_requests');
const responseTimeTrend = new Trend('custom_req_duration');

// Base URL configurable vía variable de entorno o fallback a localhost / host.docker.internal
const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:18080';

export const options = {
  stages: [
    { duration: '30s', target: 20 },  // Rampa de subida (Warm-up / Carga normal)
    { duration: '1m',  target: 50 },  // Carga sostenida moderada (50 VUs)
    { duration: '30s', target: 100 }, // Pico de carga esperada
    { duration: '30s', target: 0 },   // Rampa de bajada
  ],
  thresholds: {
    // 95% de peticiones deben responder en menos de 800ms
    http_req_duration: ['p(95)<800'],
    // Tasa de errores menor al 1%
    failed_requests: ['rate<0.01'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  // 1. Catálogo de lugares públicos (búsqueda general)
  const resLugares = http.get(`${BASE_URL}/api/lugares?page=0&size=10`, {
    headers: { 'Accept': 'application/json' },
  });

  const checkLugares = check(resLugares, {
    'GET /api/lugares status is 200': (r) => r.status === 200,
  });
  failedRequests.add(!checkLugares);
  responseTimeTrend.add(resLugares.timings.duration);

  sleep(1);

  // 2. Búsqueda con filtro por texto
  const resSearch = http.get(`${BASE_URL}/api/lugares?q=cafe&page=0&size=5`, {
    headers: { 'Accept': 'application/json' },
  });

  const checkSearch = check(resSearch, {
    'GET /api/lugares?q=... status is 200': (r) => r.status === 200,
  });
  failedRequests.add(!checkSearch);
  responseTimeTrend.add(resSearch.timings.duration);

  sleep(1);

  // 3. Intento de Login (credenciales de prueba)
  const loginPayload = JSON.stringify({
    correo: 'final@localspot.test',
    contrasena: 'password',
  });

  const loginParams = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  };

  const resLogin = http.post(`${BASE_URL}/api/auth/login`, loginPayload, loginParams);

  const checkLogin = check(resLogin, {
    'POST /api/auth/login status is 200': (r) => r.status === 200,
  });
  failedRequests.add(!checkLogin);
  responseTimeTrend.add(resLogin.timings.duration);

  sleep(1);
}
