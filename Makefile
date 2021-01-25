docker-compose:
	docker-compose up

docker-compose-dependencies:
	docker-compose -f docker-compose-dependencies.yml up

liquibase-create-change-log:
	./gradlew liquibaseDiffChangelog

liquibase-apply-change-log:
	./gradlew migratePostgresDatabase

application-run:
	./gradlew bootRun

application-smoke-test:
	./gradlew smoke -i

build-functional-test:
	./gradlew functional -i

build-integration-test:
	./gradlew integration -i``

build-test:
	./gradlew test -i
