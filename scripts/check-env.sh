#!/usr/bin/env sh
set -eu

missing=0

check_cmd() {
  command_name="$1"
  label="$2"

  if command -v "$command_name" >/dev/null 2>&1; then
    printf "[ok] %s\n" "$label"
  else
    printf "[missing] %s (%s)\n" "$label" "$command_name"
    missing=1
  fi
}

printf '%s\n' "SICAE environment check"
printf '%s\n' "-----------------------"

check_cmd git "Git"
check_cmd docker "Docker"
check_cmd java "Java 17+"
check_cmd mvn "Maven"
check_cmd node "Node.js LTS"
check_cmd npm "npm"

if command -v java >/dev/null 2>&1; then
  java -version 2>&1 | head -n 1
fi

if command -v mvn >/dev/null 2>&1; then
  mvn -version | head -n 1
fi

if command -v node >/dev/null 2>&1; then
  node --version
fi

if command -v docker >/dev/null 2>&1; then
  docker --version
  docker compose version

  if docker info >/dev/null 2>&1; then
    printf "[ok] Docker daemon\n"
  else
    printf "[missing] Docker daemon (abre Docker Desktop o inicia el servicio de Docker)\n"
    missing=1
  fi
fi

exit "$missing"
