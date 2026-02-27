#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

mkdir -p build/test-classes
javac -cp build/classes -d build/test-classes $(find tests/java -name '*.java')
java -cp build/classes:build/test-classes com.bruno.salessystem.core.SalesServiceTest

echo "Tests passed"
