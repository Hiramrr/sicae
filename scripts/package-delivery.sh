#!/usr/bin/env sh
set -eu

team_number="${1:-}"

if [ -z "$team_number" ]; then
  printf "Usage: ./scripts/package-delivery.sh <numero-equipo>\n"
  printf "Example: ./scripts/package-delivery.sh 3\n"
  exit 1
fi

archive="Equipo_${team_number}.zip"

rm -f "$archive"

zip -r "$archive" . \
  -x ".git/*" \
  -x ".env" \
  -x ".env.*" \
  -x "target/*" \
  -x "*/target/*" \
  -x "dist/*" \
  -x "*/dist/*" \
  -x "docker-data/*" \
  -x "*.log" \
  -x "Equipo_*.zip"

printf "Created %s\n" "$archive"
