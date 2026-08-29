package org.arya.banking.auth.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.external.UserService;
import org.arya.banking.auth.kafka.UserEventProducer;
import org.arya.banking.auth.service.KeyCloakManager;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.avro.LoginFailedEvent;
import org.arya.banking.common.avro.UserCreateEvent;
import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.exception.ExceptionCode;
import org.arya.banking.common.exception.KeyCloakServiceException;
import org.arya.banking.common.model.KeyCloakUser;
import org.arya.banking.common.utils.EventMetadataFactory;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

import static org.arya.banking.common.exception.ExceptionCode.*;
import static org.arya.banking.common.exception.ExceptionConstants.*;
import static org.arya.banking.common.utils.CommonUtils.isNotEmpty;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {

    public static final String LOCKED = "LOCKED";
    public static final String UN_LOCKED = "UN-LOCKED";
    private final KeyCloakManager keyCloakManager;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final UsersResource usersResource;
    private final UserEventProducer userEventProducer;

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ACCESS_TOKEN = "access_token";

    @Override
    public KeyCloakResponse createKeyCloakUser(KeyCloakUser keyCloakUser) {

        log.info("Processing KeyCloak User: {}", keyCloakUser.getUsername());
        UserRepresentation userRepresentation = getUserRepresentation(keyCloakUser);

        Response response;
        try {
            response = usersResource.create(userRepresentation);
        } catch (Exception e) {
            throw new KeyCloakServiceException(500, AUTH_KEYCLOAK_INTERNAL_SERVER_ERROR_500, e.getCause().getMessage());
        }
        if(response.getStatus() != 201 ) {
            throw new KeyCloakServiceException(response.getStatus(), AUTH_KEYCLOAK_USER_CREATION_FAILED_400, "Error occurred while creating user in keycloak: "+response.readEntity(String.class));
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
        formData.add(USERNAME, username.toLowerCase());
        formData.add(PASSWORD, password);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, httpHeaders);

        ResponseEntity<HashMap> response = restTemplate.exchange(keyCloakManager.getTokenUrl(),
                HttpMethod.POST, requestEntity, HashMap.class);
        if(isNotEmpty(response) && response.getStatusCode().value() == 401) {
            updateLoginFailedAttempts(username);
            throw new KeyCloakServiceException(UN_AUTHORIZED_ERROR_CODE, SECURITY_INVALID_CREDENTIALS_401, "Invalid username or password");
        } else if(response.getStatusCode().value() == BAD_REQUEST_ERROR_CODE) {
            if(isNotEmpty(response.getBody()) && response.getBody().get("error_description").equals("Account disabled")) {
                throw new KeyCloakServiceException(FORBIDDEN_ERROR_CODE, ACCOUNT_LOCKED_403, "Account is locked. Please contact administrator.");
            }
        }
        return response.getBody().get(ACCESS_TOKEN).toString();
    }

    private void updateLoginFailedAttempts(String username) {
        log.info("Send failed login attempt for user: {}", username);
        LoginFailedEvent loginFailedEvent = LoginFailedEvent.newBuilder()
                .setMetadata(EventMetadataFactory.newEventMetadata())
                .setUserId(username)
                .setIsLockUser(true).build();
        userEventProducer.sendLoginFailedEvent(loginFailedEvent);
    }

    @Override
    public UserRepresentation findUserByUsername(String username) {

        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        if(userRepresentations.isEmpty()) {
            throw new KeyCloakServiceException(404, ExceptionCode.USER_NOT_FOUND_404, "User not found with emailId: "+username);
        }
        return userRepresentations.get(0);
    }

    @Override
    public List<UserRepresentation> findUserByUserId(String userId) {
        return List.of();
    }

    @Override
    public void onUserUpdateEvent(UserCreateEvent userCreateEvent) {
        UserRepresentation userRepresentation = findUserByUsername(userCreateEvent.getUserId().toString());
        userRepresentation.setEnabled(
                !"BLOCKED".equals(userCreateEvent.getStatus().toString())
        );
        usersResource.get(userRepresentation.getId()).update(userRepresentation);
    }
}
