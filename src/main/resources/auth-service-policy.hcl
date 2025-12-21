# Data access
path "secret/data/arya-banking/auth-service" {
  capabilities = ["read"]
}

path "secret/data/arya-banking/auth-service/*" {
  capabilities = ["read"]
}

# Metadata access (REQUIRED for KV v2)
path "secret/metadata/arya-banking/auth-service" {
  capabilities = ["read"]
}

path "secret/metadata/arya-banking/auth-service/*" {
  capabilities = ["read"]
}
