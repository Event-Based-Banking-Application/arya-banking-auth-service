package org.arya.banking.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.core.dto.KeyCloakResponse;
import org.arya.banking.common.core.model.KeyCloakUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/auth")
@Tag(name = "Internal Auth", description = "Internal service-to-service authentication endpoints for Keycloak user registration")
public class InternalKeyCloakController {

    private final KeyCloakService keyCloakService;

    @PostMapping("/register/users")
    @Operation(summary = "Register Keycloak user", description = "Creates a new user in Keycloak. Called internally by the user-service during registration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully in Keycloak"),
            @ApiResponse(responseCode = "409", description = "User already exists in Keycloak")
    })
    public ResponseEntity<KeyCloakResponse> registerUser(@RequestBody KeyCloakUser keyCloakUser) {
        return ResponseEntity.ok(keyCloakService.createKeyCloakUser(keyCloakUser));
    }
}
