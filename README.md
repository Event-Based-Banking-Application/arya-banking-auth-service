# Arya Banking Auth Service

Identity bridge between the Arya Banking platform and Keycloak — handles authentication, user onboarding, and account security.

## Quick Start

```powershell
# Prerequisites: Docker infra running, Vault unsealed, common library built
mvn clean spring-boot:run
```

The service starts on port **8087** and registers as `ARYA-BANKING-AUTH-SERVICE` in Eureka.

## Links

- [Local Development Setup](https://event-based-banking-application.github.io/arya-banking/docs/local-development/)
- [Auth Service Docs](https://event-based-banking-application.github.io/arya-banking/docs/auth-service/)
