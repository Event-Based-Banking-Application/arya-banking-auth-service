package org.arya.banking.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.arya.banking.auth.service.KeyCloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Public authentication endpoints for user login via Keycloak")
public class KeyCloakController {

    private final KeyCloakService keyCloakService;

    @GetMapping("/authenticate")
    @Operation(summary = "Authenticate user", description = "Authenticates a user against Keycloak using username and password. Returns a JWT access token on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<String> authenticate(
            @Parameter(description = "Keycloak username") @RequestParam String username,
            @Parameter(description = "Keycloak password") @RequestParam String password) {
        return ResponseEntity.ok(keyCloakService.authenticateUser(username, password));
    }
}
