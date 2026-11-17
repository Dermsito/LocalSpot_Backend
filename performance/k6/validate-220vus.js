import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const failedRequests = new Rate('failed_requests');
const responseTimeTrend = new Trend('custom_req_duration');

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:18080';

export const options = {
  stages: [
    { duration: '20s', target: 220 }, // Rampa rápida hacia 220 VUs
    { duration: '1m',  target: 220 }, // Carga sostenida a 220 VUs
    { duration: '15s', target: 0 },   // Rampa de bajada
  ],
  thresholds: {
    http_req_duration: ['p(95)<400'],
    failed_requests: ['rate<0.01'],
  },
};

export default function () {
  const resLugares = http.get(`${BASE_URL}/api/lugares?page=0&size=20`, {
    headers: { 'Accept': 'application/json' },
  });

  const checkOk = check(resLugares, {
    'GET /api/lugares status is 200': (r) => r.status === 200,
  });

  failedRequests.add(!checkOk);
  responseTimeTrend.add(resLugares.timings.duration);

  sleep(0.5);

  const resSearch = http.get(`${BASE_URL}/api/lugares?q=zona&page=0&size=10`, {
    headers: { 'Accept': 'application/json' },
  });

  const checkSearch = check(resSearch, {
    'GET /api/lugares?q=zona status is 200': (r) => r.status === 200,
  });

  failedRequests.add(!checkSearch);
  responseTimeTrend.add(resSearch.timings.duration);

  sleep(0.5);
}
