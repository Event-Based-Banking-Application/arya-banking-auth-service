package org.arya.banking.auth.external;

import org.arya.banking.common.config.FeignConfiguration;
import org.arya.banking.common.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "ARYA-BANKING-USER-SERVICE", configuration = FeignConfiguration.class)
public interface UserService {

    @PutMapping("/internal/api/security-details/{userId}")
    ResponseEntity<Map<String, String>> updateLoginAttempts(@PathVariable String userId, @RequestParam boolean loginFailed);
}
