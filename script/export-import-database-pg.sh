#!/usr/bin/env bash
set -euo pipefail

# =========================
# CONFIG
# =========================
NEON_HOST="**************"
NEON_PORT="**************"
NEON_USER="**************"
PGPASSWORD="*************"              # Neon password
NEON_DB="sewearn"
SSL_MODE="require"

DUMP_DIR="/home/root320/Desktop/Practice/Project/sewearn-project/data/database-dump"

LOCAL_CONTAINER_NAME="sewearn-local-pg17"
LOCAL_PG_USER="postgres"
LOCAL_PG_PASSWORD="postgres"
LOCAL_PG_IMAGE="postgres:17"

# =========================
# PRE-CHECKS
# =========================
command -v docker >/dev/null 2>&1 || { echo "ERROR Docker is not installed"; exit 1; }

mkdir -p "$DUMP_DIR"

TS="$(date +"%Y-%m-%d_%H-%M-%S")"
SQL_FILE="${DUMP_DIR}/sewearn-data-dump-${TS}.sql"
LOCAL_DB="sewearn-${TS}"

echo "Timestamp ${TS}"
echo "SQL Dump File ${SQL_FILE}"
echo "Local Container ${LOCAL_CONTAINER_NAME}"
echo "Local Database ${LOCAL_DB}"
echo

# =========================
# CHECK Docker daemon running
# =========================
if ! docker info >/dev/null 2>&1; then
  echo "ERROR Docker daemon is not running. Start Docker and try again."
  exit 1
fi

# =========================
# CHECK Local Postgres container running (do not auto start)
# =========================
if ! docker ps --format '{{.Names}}' | grep -qx "${LOCAL_CONTAINER_NAME}"; then
  echo "ERROR Local container '${LOCAL_CONTAINER_NAME}' is not running. Start it and try again."
  echo "Command docker start ${LOCAL_CONTAINER_NAME}"
  exit 1
fi

# =========================
# STEP 1 Export Neon -> SQL
# =========================
echo "1/2 Exporting Neon database to SQL..."

docker run --rm -i \
  -e PGSSLMODE="${SSL_MODE}" \
  -e PGPASSWORD="${PGPASSWORD}" \
  -v "${DUMP_DIR}:/backup" \
  "${LOCAL_PG_IMAGE}" \
  bash -lc "
    pg_dump \
      -h ${NEON_HOST} \
      -p ${NEON_PORT} \
      -U ${NEON_USER} \
      -d ${NEON_DB} \
      --no-owner \
      --no-acl \
      -F p \
      -f /backup/$(basename "${SQL_FILE}")
  "

echo "Export completed"
ls -lh "${SQL_FILE}"
echo

# =========================
# STEP 2 Create DB and import into existing container
# =========================
echo "2/2 Creating local database and importing data..."

# Wait until Postgres is ready
echo "Waiting for local Postgres..."
for i in {1..60}; do
  if docker exec "${LOCAL_CONTAINER_NAME}" pg_isready -U "${LOCAL_PG_USER}" >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

# Create DB (fail if exists)
docker exec -e PGPASSWORD="${LOCAL_PG_PASSWORD}" "${LOCAL_CONTAINER_NAME}" \
  psql -U "${LOCAL_PG_USER}" -d postgres -v ON_ERROR_STOP=1 \
  -c "CREATE DATABASE \"${LOCAL_DB}\";"

# Import SQL
docker exec -i -e PGPASSWORD="${LOCAL_PG_PASSWORD}" "${LOCAL_CONTAINER_NAME}" \
  psql -U "${LOCAL_PG_USER}" -d "${LOCAL_DB}" -v ON_ERROR_STOP=1 < "${SQL_FILE}"

echo
echo "Done ✅"
echo "Backup File ${SQL_FILE}"
echo "Local DB ${LOCAL_DB}"
echo "pgAdmin Host localhost"
echo "pgAdmin Port 5433"
echo "User ${LOCAL_PG_USER}"
echo "Password ${LOCAL_PG_PASSWORD}"
