#!/bin/bash

## Usage: ./docker/dependencies/start-local-environment DOCMOSIS_ACCESS_KEY
##
## Options:
##    - DOCMOSIS_ACCESS_KEY: Access key for docmosis development environment.
##
## Start local environment including idam client setup.


# Set variables
COMPOSE_FILE="-f docker-compose-ccd-dependencies.yml"
IDAM_URI="http://localhost:5000"
IDAM_USERNAME="idamOwner@hmcts.net"
IDAM_PASSWORD="Ref0rmIsFun"
SYSTEM_USERNAME="em.hrs.api@hmcts.net.internal"
SYSTEM_PASSWORD="Passw0rd0!"
export DOCMOSIS_ACCESS_KEY=$1


# Start IDAM setup
echo "Starting shared-db..."
docker-compose ${COMPOSE_FILE} up -d shared-db

echo "Starting IDAM(ForgeRock)..."
docker-compose ${COMPOSE_FILE} up -d fr-am
docker-compose ${COMPOSE_FILE} up -d fr-idm

echo "Starting IDAM..."
docker-compose ${COMPOSE_FILE} up -d idam-api

echo "Testing IDAM Authentication..."
token=$(./docker/dependencies/idam-authenticate.sh ${IDAM_URI} ${IDAM_USERNAME} ${IDAM_PASSWORD})
while [ "_${token}" = "_" ]; do
      sleep 60
      echo "idam-api is not running! Check logs, you may need to restart"
      token=$(./docker/dependencies/idam-authenticate.sh ${IDAM_URI} ${IDAM_USERNAME} ${IDAM_PASSWORD})
done

# Set up IDAM client with services and roles
echo "Setting up IDAM client..."
(./docker/dependencies/idam-client-setup.sh ${IDAM_URI} services ${token} '{"description": "em", "label": "em", "oauth2ClientId": "webshow", "oauth2ClientSecret": "AAAAAAAAAAAAAAAA", "oauth2RedirectUris": ["http://localhost:8080/oauth2redirect"], "selfRegistrationAllowed": true}')
(./docker/dependencies/idam-client-setup.sh ${IDAM_URI} services ${token} '{"description": "ccd gateway", "label": "ccd gateway", "oauth2ClientId": "ccd_gateway", "oauth2ClientSecret": "AAAAAAAAAAAAAAAA", "oauth2RedirectUris": ["http://localhost:3451/oauth2redirect"], "selfRegistrationAllowed": true}')
(./docker/dependencies/idam-client-setup-roles.sh ${IDAM_URI} ${token} caseworker)
(./docker/dependencies/idam-client-setup-roles.sh ${IDAM_URI} ${token} caseworker-hrs)
(./docker/dependencies/idam-client-setup-roles.sh ${IDAM_URI} ${token} ccd-import)
(./docker/dependencies/idam-create-hrs-system-user.sh ${IDAM_URI} ${SYSTEM_USERNAME} ${SYSTEM_PASSWORD})

# Start all other images
echo "Starting dependencies..."
docker-compose ${COMPOSE_FILE} build
docker-compose ${COMPOSE_FILE} up -d shared-database \
                                     em-hrs-db \
                                     service-auth-provider-api \
                                     azure-storage-emulator-azurite \
                                     ccd-user-profile-api \
                                     ccd-definition-store-api \
                                     ccd-data-store-api \
                                     ccd-api-gateway \
                                     smtp-server
echo "LOCAL ENVIRONMENT SUCCESSFULLY STARTED"
