package org.arya.banking.auth.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.service.KeyCloakManager;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.exception.InternalServerExceptionHandler;
import org.arya.banking.common.exception.KeyCloakServiceException;
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

import static org.arya.banking.common.exception.ExceptionCode.KEYCLOAK_INTERNAL_SERVER_CODE;
import static org.arya.banking.common.exception.ExceptionCode.KEYCLOAK_USER_CREATION_CODE;

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
    public KeyCloakResponse createKeyCloakUser(KeyCloakUser keyCloakUser) {

        log.info("Processing KeyCloak User: {}", keyCloakUser);
        UserRepresentation userRepresentation = getUserRepresentation(keyCloakUser);

        log.info("Processing keycloak User Representation: {}", userRepresentation);

        Response response;
        try {
            response = keyCloakManager.getKeyCloakInstanceWithRealm().users().create(userRepresentation);
        } catch (Exception e) {
            throw new KeyCloakServiceException(500, KEYCLOAK_INTERNAL_SERVER_CODE, e.getCause().getMessage());
        }
        if(response.getStatus() != 201 ) {
            throw new KeyCloakServiceException(response.getStatus(), KEYCLOAK_USER_CREATION_CODE, "Error occurred while creating user in keycloak: "+response.readEntity(String.class));
        }
        return new KeyCloakResponse(String.valueOf(response.getStatus()), response.readEntity(String.class));
    }

    private static UserRepresentation getUserRepresentation(KeyCloakUser keyCloakUser) {
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
        return userRepresentation;
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
