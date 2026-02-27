SHELL := /bin/bash
BUILD_DIR := build/classes
TEST_BUILD_DIR := build/test-classes

.PHONY: build run test clean docker-up docker-down

build:
	./scripts/build.sh

run: build
	./scripts/run_server.sh

test: build
	./scripts/test.sh

clean:
	rm -rf build

docker-up:
	docker compose up --build

docker-down:
	docker compose down
