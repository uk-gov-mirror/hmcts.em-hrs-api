#!/bin/bash

## Usage: ./bin/idam-authenticate.sh IDAM_URI SYSTEM_USERNAME SYSTEM_PASSWORD
##
##
## Make call to IDAM to get auth token

IDAM_URI="http://localhost:5000"
SYSTEM_USERNAME="em.hrs.api@hmcts.net.internal"
SYSTEM_PASSWORD="passw0rd0!"
USER_ROLES="[{ \"code\":\"caseworker\"}, {\"code\":\"caseworker-hrs\" }]"
USER_DETAILS="{ \"email\": ${SYSTEM_PASSWORD}, \"forename\": \"system\", \"surname\": \"user\", \"password\": ${SYSTEM_PASSWORD}, \"roles\": ${USER_ROLES}"

curl -X POST "${IDAM_URI}/testing-support/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "${USER_DETAILS}"
