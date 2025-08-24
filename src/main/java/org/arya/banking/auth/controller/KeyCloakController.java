package org.arya.banking.auth.controller;

import lombok.RequiredArgsConstructor;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.model.KeyCloakUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KeyCloakController {

    private final KeyCloakService keyCloakService;

    @PostMapping("/register/users")
    public ResponseEntity<Integer> registerUser(@RequestBody KeyCloakUser keyCloakUser) {
        return ResponseEntity.ok(keyCloakService.createKeyCloakUser(keyCloakUser));
    }
}
