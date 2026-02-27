#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

PORT="${PORT:-8080}"
PROLOG_DATA_FILE="${PROLOG_DATA_FILE:-src/main/resources/prolog/store.pl}"
DEBUG="${DEBUG:-false}"

if [[ ! -d build/classes ]]; then
  ./scripts/build.sh
fi

export PORT
export PROLOG_DATA_FILE
export DEBUG

java -cp build/classes com.bruno.salessystem.api.ApiServer
