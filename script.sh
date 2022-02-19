#!/usr/bin/env sh

until nc -z "${MONGO:-mongo}" "${MONGODB_PORT:-27017}"; do
  echo "$(date) - waiting for mongo..."
  sleep 5
done

./gradlew run 
