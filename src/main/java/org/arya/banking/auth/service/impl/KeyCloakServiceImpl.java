package org.arya.banking.auth.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.service.KeyCloakManager;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.model.KeyCloakUser;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {

    private final KeyCloakManager keyCloakManager;

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ACCESS_TOKEN = "access_token";

    @Override
    public Response createKeyCloakUser(KeyCloakUser keyCloakUser) {

        log.info("Processing KeyCloak User: {}", keyCloakUser);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(keyCloakUser.getUsername());
        userRepresentation.setFirstName(keyCloakUser.getFirstName());
        userRepresentation.setLastName(keyCloakUser.getLastName());
        userRepresentation.setEmail(keyCloakUser.getEmailId());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(keyCloakUser.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(List.of(credentialRepresentation));

        log.info("Processing keycloak User Representation: {}", userRepresentation);

        return keyCloakManager.getKeyCloakInstanceWithRealm().users().create(userRepresentation);
    }

    @Override
    public String authenticateUser(String username, String password) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(CLIENT_ID, keyCloakManager.getClientId());
        formData.add(CLIENT_SECRET, keyCloakManager.getClientSecret());
        formData.add(GRANT_TYPE, PASSWORD);
        formData.add(USERNAME, username);
        formData.add(PASSWORD, password);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<HashMap> response = restTemplate.exchange(keyCloakManager.getTokenUrl(),
                HttpMethod.POST, requestEntity, HashMap.class);

        log.info("Response from token endpoint: {}", response.getBody());

        return response.getBody().get(ACCESS_TOKEN).toString();
    }

    @Override
    public List<UserRepresentation> findUserByEmailId(String emailId) {
        return List.of();
    }

    @Override
    public List<UserRepresentation> findUserByUserId(String userId) {
        return List.of();
    }
}
