#!/bin/bash

## Usage: ./bin/idam-authenticate.sh IDAM_URI SYSTEM_USERNAME SYSTEM_PASSWORD
##
##
## Make call to IDAM to get auth token

IDAM_URI="http://localhost:5000"
SYSTEM_USERNAME="another.user@hmcts.net"
SYSTEM_PASSWORD="passw0rd0!"
USER_DETAILS="{ \"email\": \"another.user@hmcts.net\", \"forename\": \"system\", \"surname\": \"user\", \"password\": \"${SYSTEM_PASSWORD}\", \"roles\":[{ \"code\":\"caseworker\"}, {\"code\":\"caseworker-hrs\" }]}"

curl -X POST "${IDAM_URI}/testing-support/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "${USER_DETAILS}"
