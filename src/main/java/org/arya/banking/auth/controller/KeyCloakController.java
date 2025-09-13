package org.arya.banking.auth.controller;

import lombok.RequiredArgsConstructor;
import org.arya.banking.auth.service.KeyCloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KeyCloakController {

    private final KeyCloakService keyCloakService;

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(keyCloakService.authenticateUser(username, password));
    }
}
