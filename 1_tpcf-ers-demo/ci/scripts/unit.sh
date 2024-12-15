#!/usr/bin/env bash
set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd git-repo
./mvnw -v
./mvnw test
