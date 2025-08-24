package org.arya.banking.auth.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KeyCloakManager {

    @Value("${app.config.keycloak.url}")
    private String serverUrl;

    @Value("${app.config.keycloak.realm}")
    private String keyCloakRealm;

    @Value("${app.config.keycloak.client-id}")
    private String clientId;

    @Value("${app.config.keycloak.client-secret}")
    private String clientSecret;

    @Value("${app.config.keycloak.token-uri}")
    private String tokenUrl;

    private Keycloak keycloak;

    private static final String CLIENT_CREDENTIALS = "client_credentials";

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(keyCloakRealm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(CLIENT_CREDENTIALS)
                .build();
    }

    public RealmResource getKeyCloakInstanceWithRealm() {
        return keycloak.realm(keyCloakRealm);
    }
}
