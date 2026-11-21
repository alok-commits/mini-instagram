package com.mininstagram.common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;

/** Uses internal JWKS retrieval but validates the PUBLIC issuer and API audience. */
@Configuration
public class JwtVerifierConfig {
 @Bean
 JwtDecoder jwtDecoder(@Value("${security.issuer-uri}") String issuer,
                       @Value("${security.jwk-set-uri}") String jwks,
                       @Value("${security.audience}") String audience) {
   NimbusJwtDecoder decoder=NimbusJwtDecoder.withJwkSetUri(jwks).build();
   OAuth2TokenValidator<Jwt> audienceValidator = jwt -> jwt.getAudience().contains(audience)
       ? OAuth2TokenValidatorResult.success()
       : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Invalid API audience",null));
   decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), audienceValidator));
   return decoder;
 }
}
