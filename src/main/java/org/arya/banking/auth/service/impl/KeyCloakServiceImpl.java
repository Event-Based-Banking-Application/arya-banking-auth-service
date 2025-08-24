package org.arya.banking.auth.service.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.service.KeyCloakManager;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.model.KeyCloakUser;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {

    private final KeyCloakManager keyCloakManager;

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
    public List<UserRepresentation> findUserByEmailId(String emailId) {
        return List.of();
    }

    @Override
    public List<UserRepresentation> findUserByUserId(String userId) {
        return List.of();
    }
}
