package com.mininstagram.gateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.List;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class GatewaySecurityConfig {
 @Bean SecurityWebFilterChain api(ServerHttpSecurity http){
   return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(e -> e.pathMatchers("/actuator/health/**","/actuator/prometheus").permitAll()
         .pathMatchers("/actuator/**").denyAll()
         .anyExchange().permitAll())
      .oauth2ResourceServer(o -> o.jwt(withDefaults())).build();
 }
 @Bean ReactiveJwtDecoder reactiveJwtDecoder(@Value("${security.issuer-uri}") String issuer,
   @Value("${security.jwk-set-uri}") String jwks,@Value("${security.audience}") String aud){
   NimbusReactiveJwtDecoder decoder=NimbusReactiveJwtDecoder.withJwkSetUri(jwks).build();
   OAuth2TokenValidator<Jwt> audienceValidator = jwt -> jwt.getAudience().contains(aud)
      ? OAuth2TokenValidatorResult.success()
      : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Invalid API audience",null));
   decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer),audienceValidator));
   return decoder;
 }
 @Bean CorsWebFilter corsFilter(@Value("${frontend.origin}") String origin){
    CorsConfiguration cors=new CorsConfiguration();
    cors.setAllowedOrigins(List.of(origin));
    cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cors.setAllowedHeaders(List.of("Authorization","Content-Type"));
    cors.setExposedHeaders(List.of("Location","X-RateLimit-Remaining","X-RateLimit-Replenish-Rate","X-RateLimit-Burst-Capacity"));
    cors.setAllowCredentials(false);
    UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**",cors);
    return new CorsWebFilter(source);
 }
}
