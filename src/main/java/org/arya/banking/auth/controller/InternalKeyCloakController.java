package org.arya.banking.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.model.KeyCloakUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/auth")
public class InternalKeyCloakController {

    private final KeyCloakService keyCloakService;

    @PostMapping("/register/users")
    public ResponseEntity<KeyCloakResponse> registerUser(@RequestBody KeyCloakUser keyCloakUser) {
        return ResponseEntity.ok(keyCloakService.createKeyCloakUser(keyCloakUser));
    }
}
