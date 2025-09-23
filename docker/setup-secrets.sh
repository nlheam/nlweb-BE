#!/bin/bash
# Script to set up Docker secrets for nlweb-BE

echo "🔧 Setting up Docker secrets for nlweb-BE..."

# Create secrets directory if it doesn't exist
mkdir -p secrets

# Function to generate a random password
generate_password() {
    openssl rand -base64 32 | tr -d "=+/" | cut -c1-16
}

# Check if secret files exist, if not create them from templates or generate new ones
if [ ! -f "secrets/postgres_password.txt" ]; then
    if [ -f "secrets/postgres_password.txt.template" ]; then
        echo "📋 Using template for PostgreSQL password..."
        cp secrets/postgres_password.txt.template secrets/postgres_password.txt
    else
        echo "🔐 Generating new PostgreSQL password..."
        generate_password > secrets/postgres_password.txt
    fi
else
    echo "✅ PostgreSQL password file already exists"
fi

if [ ! -f "secrets/spring_datasource_password.txt" ]; then
    if [ -f "secrets/spring_datasource_password.txt.template" ]; then
        echo "📋 Using template for Spring datasource password..."
        cp secrets/spring_datasource_password.txt.template secrets/spring_datasource_password.txt
    else
        echo "🔐 Generating new Spring datasource password..."
        generate_password > secrets/spring_datasource_password.txt
    fi
else
    echo "✅ Spring datasource password file already exists"
fi

# Set proper permissions
chmod 600 secrets/*.txt

echo ""
echo "🛡️  Security configured successfully!"
echo "📁 Secret files created in: $(pwd)/secrets/"
echo "🔒 File permissions set to 600 (owner read/write only)"
echo ""
echo "To start the services with secrets:"
echo "   docker compose up -d"
echo ""
echo "To view this setup documentation:"
echo "   cat SECRETS.md"