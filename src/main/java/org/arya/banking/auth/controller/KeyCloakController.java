package org.arya.banking.auth.controller;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.arya.banking.auth.service.KeyCloakService;
import org.arya.banking.common.model.KeyCloakUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KeyCloakController {

    private final KeyCloakService keyCloakService;

    @PostMapping("/register/users")
    public ResponseEntity<Response> registerUser(@RequestBody KeyCloakUser keyCloakUser) {
        return ResponseEntity.ok(keyCloakService.createKeyCloakUser(keyCloakUser));
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(keyCloakService.authenticateUser(username, password));
    }
}
