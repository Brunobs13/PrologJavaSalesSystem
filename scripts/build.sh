#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

mkdir -p build/classes
javac -d build/classes $(find src/main/java -name '*.java')

echo "Build completed: build/classes"
