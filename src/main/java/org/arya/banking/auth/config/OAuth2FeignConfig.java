package org.arya.banking.auth.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.arya.banking.common.exception.ExceptionCode;
import org.arya.banking.common.exception.InvalidOAuth2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import static org.arya.banking.common.exception.ExceptionCode.AUTH_INVALID_OAUTH_TOKEN_400;
import static org.arya.banking.common.utils.CommonUtils.isEmpty;

@Configuration
@RequiredArgsConstructor
public class OAuth2FeignConfig {

    public static final String BEARER_ = "Bearer ";
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${app.security.client-registrationId}")
    private String clientRegistrationId;

    @Bean
    public RequestInterceptor oauth2RequestInterceptor() {
        return requestTemplate -> {

            OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(clientRegistrationId)
                    .principal(clientRegistrationId).build();

            OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientManager.authorize(oAuth2AuthorizeRequest);
            if(isEmpty(oAuth2AuthorizedClient)) {
                throw new InvalidOAuth2Client(400, AUTH_INVALID_OAUTH_TOKEN_400, String.format("Could not get oauth client from: %s", clientRegistrationId));
            }
            requestTemplate.header(HttpHeaders.AUTHORIZATION, BEARER_+oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        };
    }
}
