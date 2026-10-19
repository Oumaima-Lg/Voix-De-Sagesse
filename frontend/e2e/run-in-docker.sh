#!/bin/sh
# Runs the end-to-end tests without installing Node, in Playwright's own image. The container uses the
# host network, so it reaches the app at localhost like a normal browser.
set -e
cd "$(dirname "$0")/.."
# Same demo passwords as the running app, if they were changed in .env
if [ -f ../.env ]; then
  set -a
  . ../.env
  set +a
fi
# The dependencies go to a volume of the container, not to frontend/node_modules; the report is
# handed back to you at the end.
docker run --rm --network host -e HOST_USER="$(id -u):$(id -g)" \
  -e DEMO_USER_PASSWORD -e DEMO_ADMIN_PASSWORD \
  -v "$PWD":/app -v /app/node_modules -w /app mcr.microsoft.com/playwright:v1.63.0-noble \
  sh -c 'npm ci --no-audit --no-fund && npx playwright test; status=$?; chown -R "$HOST_USER" test-results playwright-report 2>/dev/null; exit $status'
