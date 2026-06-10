#!/usr/bin/env sh
set -eu

USERS_DB_NAME="sicaeUsuario"
USERS_DB_USER="sicae_usuario_app"
USERS_DB_PASSWORD="sicae123"
VEHICLES_DB_NAME="sicaevehiculo"
VEHICLES_DB_USER="sicae_vehiculo_app"
VEHICLES_DB_PASSWORD="sicae123"
PARKING_DB_NAME="sicaeEstacionamiento"
PARKING_DB_USER="sicae_estacionamiento_app"
PARKING_DB_PASSWORD="sicae123"

printf '%s\n' "Checking PostgreSQL users database..."
docker compose exec -T users-db sh -c "PGPASSWORD='$USERS_DB_PASSWORD' psql -U '$USERS_DB_USER' -d '$USERS_DB_NAME' -c '\\dt public.*' -c '\\dv public.*'"

printf '%s\n' "Checking MySQL vehicles database..."
docker compose exec -T vehicles-db sh -c "MYSQL_PWD='$VEHICLES_DB_PASSWORD' mysql -u'$VEHICLES_DB_USER' '$VEHICLES_DB_NAME' -e 'SHOW FULL TABLES;'"

printf '%s\n' "Checking MySQL parking database..."
docker compose exec -T parking-db sh -c "MYSQL_PWD='$PARKING_DB_PASSWORD' mysql -u'$PARKING_DB_USER' '$PARKING_DB_NAME' -e 'SHOW FULL TABLES;'"
