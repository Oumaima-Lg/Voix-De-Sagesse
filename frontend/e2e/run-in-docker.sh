#!/bin/sh
set -e
cd "$(dirname "$0")/.."
if [ -f ../.env ]; then
  set -a
  . ../.env
  set +a
fi
docker run --rm --network host -e HOST_USER="$(id -u):$(id -g)" \
  -e DEMO_USER_PASSWORD -e DEMO_ADMIN_PASSWORD \
  -v "$PWD":/app -v /app/node_modules -w /app mcr.microsoft.com/playwright:v1.63.0-noble \
  sh -c 'npm ci --no-audit --no-fund && npx playwright test; status=$?; chown -R "$HOST_USER" test-results playwright-report 2>/dev/null; exit $status'
