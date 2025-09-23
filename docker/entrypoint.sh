#!/bin/bash
set -e

# Read the database password from Docker secret
if [ -f /run/secrets/spring_datasource_password ]; then
    export SPRING_DATASOURCE_PASSWORD=$(cat /run/secrets/spring_datasource_password)
fi

# Execute the main command
exec "$@"