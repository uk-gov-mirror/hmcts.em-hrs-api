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
	./gradlew integration -i

build-test:
	./gradlew test -i

build-check:
	./gradlew check -i


#Note this fails if there is already a container.
sonarqube-run-local-sonarqube-server:
	docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest

# New containers will require logging in, and changing password to a temporary password, and back to admin
sonarqube-run-tests:
	./gradlew sonarqube -Dsonar.login="admin" -Dsonar.password="admin" -i


report-checkstyle:
	xdg-open build/reports/checkstyle/main.html

report-integration-tests:
	xdg-open build/reports/tests/integration/index.html

report-code-tests:
	xdg-open build/reports/tests/test/index.html
