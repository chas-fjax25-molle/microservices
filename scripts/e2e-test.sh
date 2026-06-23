#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

GATEWAY_URL="http://localhost:8080"
MAX_RETRIES=60
RETRY_INTERVAL=10

cleanup() {
    echo "=== Cleaning up ==="
    docker compose down -v
    echo "Done"
}

# In CI, let the workflow handle cleanup so logs can be captured on failure
if [ "${CI:-}" != "true" ]; then
    trap cleanup EXIT
fi

echo "=== Building Docker images ==="
docker compose build

echo "=== Starting stack ==="
docker compose up -d

echo "=== Waiting for API Gateway to be ready ==="
for i in $(seq 1 "$MAX_RETRIES"); do
    if curl -s -o /dev/null --connect-timeout 5 "$GATEWAY_URL/" 2>/dev/null; then
        echo "API Gateway is ready!"
        break
    fi
    if [ "$i" -eq "$MAX_RETRIES" ]; then
        echo "ERROR: API Gateway did not become ready in time"
        docker compose logs --tail=50 api-gateway service-registry vault
        exit 1
    fi
    echo "Waiting... (${i}/${MAX_RETRIES})"
    sleep "$RETRY_INTERVAL"
done

echo ""
echo "=== 1. Register user ==="
REGISTER_RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/gateway/users/register" \
    -H "Content-Type: application/json" \
    -d '{"username":"e2euser","email":"e2e@test.com","password":"password123"}')
echo "Response: $REGISTER_RESPONSE"

USER_ID=$(echo "$REGISTER_RESPONSE" | jq -r '.id')
USERNAME=$(echo "$REGISTER_RESPONSE" | jq -r '.username')
if [ "$USERNAME" != "e2euser" ] || [ -z "$USER_ID" ] || [ "$USER_ID" = "null" ]; then
    echo "FAIL: Registration failed"
    exit 1
fi
echo "PASS: User registered (ID: $USER_ID)"

echo ""
echo "=== 2. Login ==="
LOGIN_RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/gateway/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"e2euser","password":"password123"}')
echo "Response: $LOGIN_RESPONSE"

JWT_TOKEN="$LOGIN_RESPONSE"
JWT_PARTS=$(echo "$JWT_TOKEN" | awk -F'.' '{print NF}')
if [ "$JWT_PARTS" -ne 3 ]; then
    echo "FAIL: Login did not return a valid JWT (expected 3 parts, got $JWT_PARTS)"
    exit 1
fi
echo "PASS: Login successful, JWT received"

echo ""
echo "=== 3. Create event ==="
FUTURE_TIME=$(date -d "+2 hours" -u +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date -v+2H -u +"%Y-%m-%dT%H:%M:%S")
EVENT_RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/booking-service/events" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -d "$(cat <<EOF
{
    "name": "E2E Test Event",
    "description": "Created during E2E test",
    "time": "${FUTURE_TIME}",
    "place": "Test Location",
    "capacity": 100
}
EOF
)")
echo "Response: $EVENT_RESPONSE"

EVENT_ID=$(echo "$EVENT_RESPONSE" | jq -r '.id')
EVENT_NAME=$(echo "$EVENT_RESPONSE" | jq -r '.name')
if [ "$EVENT_NAME" != "E2E Test Event" ] || [ -z "$EVENT_ID" ] || [ "$EVENT_ID" = "null" ]; then
    echo "FAIL: Event creation failed"
    exit 1
fi
echo "PASS: Event created (ID: $EVENT_ID)"

echo ""
echo "=== 4. Create booking ==="
BOOKING_RESPONSE=$(curl -s -X POST "$GATEWAY_URL/api/booking-service/bookings" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -d "$(cat <<EOF
{
    "eventId": "${EVENT_ID}",
    "userId": "${USER_ID}"
}
EOF
)")
echo "Response: $BOOKING_RESPONSE"

BOOKING_ID=$(echo "$BOOKING_RESPONSE" | jq -r '.id')
if [ -z "$BOOKING_ID" ] || [ "$BOOKING_ID" = "null" ]; then
    echo "FAIL: Booking creation failed"
    exit 1
fi
echo "PASS: Booking created (ID: $BOOKING_ID)"

echo ""
echo "=== 5. Verify booking by user ==="
VERIFY_RESPONSE=$(curl -s "$GATEWAY_URL/api/booking-service/bookings/user/${USER_ID}" \
    -H "Authorization: Bearer $JWT_TOKEN")
echo "Response: $VERIFY_RESPONSE"

BOOKING_COUNT=$(echo "$VERIFY_RESPONSE" | jq 'length')
FIRST_BOOKING_ID=$(echo "$VERIFY_RESPONSE" | jq -r '.[0].id')
if [ "$BOOKING_COUNT" -lt 1 ] || [ "$FIRST_BOOKING_ID" != "$BOOKING_ID" ]; then
    echo "FAIL: Booking verification failed"
    exit 1
fi
echo "PASS: Booking verified (found $BOOKING_COUNT booking(s))"

echo ""
echo "========================================"
echo " All E2E tests passed! "
echo "========================================"

cleanup
