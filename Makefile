SHELL := /bin/sh

.PHONY: check-env up-db tools logs-db ps down reset-db verify-db package

check-env:
	./scripts/check-env.sh

up-db:
	docker compose up -d users-db vehicles-db parking-db

tools:
	docker compose --profile tools up -d adminer

logs-db:
	docker compose logs -f users-db vehicles-db parking-db

ps:
	docker compose ps

down:
	docker compose down

reset-db:
	docker compose down -v

verify-db:
	./scripts/db-smoke-test.sh

package:
	@if [ -z "$(TEAM)" ]; then \
		echo "Usage: make package TEAM=3"; \
		exit 1; \
	fi
	./scripts/package-delivery.sh "$(TEAM)"
