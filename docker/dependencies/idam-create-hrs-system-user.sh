#!/bin/bash

## Usage: ./docker/dependencies/idam-authenticate.sh IDAM_URI SYSTEM_USERNAME SYSTEM_PASSWORD
##
##
## Make call to IDAM to get auth token

IDAM_URI=$1
USERNAME=$2
PASSWORD=$3
ROLES=$4
USER_DETAILS="{ \"email\": ${USERNAME}, \"forename\": \"system\", \"surname\": \"user\", \"password\": ${PASSWORD}, \"roles\": ${ROLES}"

curl -X POST "${IDAM_URI}/testing-support/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "${USER_DETAILS}"
