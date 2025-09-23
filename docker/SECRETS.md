# Docker Secrets Configuration

This directory contains Docker secrets configuration for sensitive data like passwords.

## Setup

1. **Create secret files** (if they don't exist):
   ```bash
   mkdir -p secrets
   echo "your-postgres-password" > secrets/postgres_password.txt
   echo "your-spring-datasource-password" > secrets/spring_datasource_password.txt
   ```

2. **Set appropriate permissions**:
   ```bash
   chmod 600 secrets/*.txt
   ```

## Security Notes

- Secret files are excluded from version control via `.gitignore`
- Store actual production passwords securely and never commit them
- In production, consider using external secret management systems like:
  - AWS Secrets Manager
  - Azure Key Vault  
  - HashiCorp Vault
  - Kubernetes Secrets

## Usage

The secrets are automatically mounted to containers:
- PostgreSQL: Uses `/run/secrets/postgres_password` 
- Spring Boot: Password is read by entrypoint script and set as environment variable

## Files

- `secrets/postgres_password.txt` - PostgreSQL password
- `secrets/spring_datasource_password.txt` - Spring Boot datasource password
- `entrypoint.sh` - Script to load Spring Boot password from secret