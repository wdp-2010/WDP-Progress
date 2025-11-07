#!/bin/bash

# WDP-Progress deployment script (adapted from SkillCoins/AuraScripts deployments)
# NOTE: This project currently contains no plugin source. This script expects a built JAR
# in the project's target directory (after running `mvn package`).

# === CONFIGURATION - edit these for your environment ===
# Full container ID (UUID) or name used by your environment
CONTAINER_ID="b8f24891-b5be-4847-a96e-c705c500aece"
# Path to the server (Pterodactyl volumes path or other mounted server path)
SERVER_DIR="/var/lib/pterodactyl/volumes/b8f24891-b5be-4847-a96e-c705c500aece"
# Plugins directory inside the server
PLUGINS_DIR="${SERVER_DIR}/plugins"
# Owner for plugin files
FILE_OWNER="pterodactyl:pterodactyl"

# === End configuration ===

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}✓${NC} $1"; }
log_error() { echo -e "${RED}✗${NC} $1"; }
log_warn() { echo -e "${YELLOW}!${NC} $1"; }

# Check root
if [[ $EUID -ne 0 ]]; then
  log_error "This script must be run as root"
  exit 1
fi

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR" || exit 1

log "Project dir: ${PROJECT_DIR}"

# Build step: run mvn package (skip tests by default)
log "Building project with Maven (this will also trigger this script via exec plugin only when run from Maven)..."
MVN_LOG="/tmp/wdp_progress_build_$(date +%s).log"
# Run mvn package only if not already called by Maven; detect MAVEN_CMD_LINE_ARGS or MAVEN_EXECUTION (best-effort)
if [[ -z "$MAVEN_EXECUTION" && -z "$MAVEN_CMD_LINE_ARGS" ]]; then
  mvn clean package -DskipTests > "$MVN_LOG" 2>&1
  BUILD_EXIT=$?
  if [[ $BUILD_EXIT -ne 0 ]]; then
    log_error "Maven build failed. Check $MVN_LOG"
    tail -n 50 "$MVN_LOG"
    exit 1
  fi
  log_success "Maven build completed"
else
  log "Detected Maven wrapper; assuming build already ran (invoked by Maven)."
fi

# Locate the built JAR (pick the newest jar in target/)
JAR_FILE="$(ls -1t target/*.jar 2>/dev/null | head -n1 || true)"
if [[ -z "$JAR_FILE" ]]; then
  log_error "No built JAR found in target/. Ensure the project was packaged."
  exit 1
fi
JAR_NAME="$(basename "$JAR_FILE")"
log_success "Found JAR: ${JAR_NAME}"

# Check server paths
if [[ -z "$CONTAINER_ID" || "$CONTAINER_ID" == "REPLACE_WITH_CONTAINER_ID" ]]; then
  log_warn "CONTAINER_ID is not configured. Edit deploy.sh and set CONTAINER_ID to your server container ID/name before deploying."
fi
if [[ ! -d "$PLUGINS_DIR" ]]; then
  log_warn "Plugins directory not found: ${PLUGINS_DIR}. Creating it (if this path is wrong, edit deploy.sh)."
  mkdir -p "$PLUGINS_DIR"
fi

# Stop container (best-effort)
log "Stopping container ${CONTAINER_ID} (if running)"
if docker ps -q --filter "id=${CONTAINER_ID}" | grep -q .; then
  docker stop "$CONTAINER_ID" >/dev/null 2>&1
  # wait for stop
  sleep 3
  log_success "Container stopped"
else
  log_warn "Container was not running or not found by ID; continue with deployment"
fi

# Backup any existing jar
if [[ -f "${PLUGINS_DIR}/${JAR_NAME}" ]]; then
  BACKUP_DIR="${SERVER_DIR}/backups/wdp-progress"
  mkdir -p "$BACKUP_DIR"
  TIMESTAMP=$(date +%Y%m%d_%H%M%S)
  cp "${PLUGINS_DIR}/${JAR_NAME}" "${BACKUP_DIR}/${JAR_NAME}.${TIMESTAMP}.bak"
  log_success "Backed up existing JAR to ${BACKUP_DIR}/${JAR_NAME}.${TIMESTAMP}.bak"
fi

# Remove existing plugin files/folders if appropriate (safe default: do not remove configs)
log "Copying new JAR to plugins directory"
cp "$JAR_FILE" "${PLUGINS_DIR}/"
if [[ $? -ne 0 ]]; then
  log_error "Failed to copy JAR to ${PLUGINS_DIR}"
  exit 1
fi
chown $FILE_OWNER "${PLUGINS_DIR}/${JAR_NAME}" 2>/dev/null || log_warn "Failed to chown (you may need to adjust FILE_OWNER)"
log_success "JAR deployed to ${PLUGINS_DIR}/${JAR_NAME}"

# Start container
log "Starting container ${CONTAINER_ID}"
docker start "$CONTAINER_ID" >/dev/null 2>&1 || log_warn "docker start failed (container may not exist or docker not available)"
log_success "Start command issued (verify server status in Pterodactyl)"

log_success "Deployment script finished"
exit 0
