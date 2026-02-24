import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 20 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.05'],
  },
};

function getToken() {
  const res = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify({
    username: 'admin',
    password: 'admin123',
  }), { headers: { 'Content-Type': 'application/json' } });
  if (!check(res, { 'login status 200': (r) => r.status === 200 })) return null;
  return JSON.parse(res.body).token;
}

export default function () {
  const token = getToken();
  if (!token) {
    sleep(1);
    return;
  }
  const headers = { Authorization: `Bearer ${token}` };

  const listRes = http.get(`${BASE_URL}/api/v1/beneficios`, { headers });
  check(listRes, { 'list status 200': (r) => r.status === 200 });

  const healthRes = http.get(`${BASE_URL}/actuator/health`);
  check(healthRes, { 'health status 200': (r) => r.status === 200 });

  sleep(1);
}
