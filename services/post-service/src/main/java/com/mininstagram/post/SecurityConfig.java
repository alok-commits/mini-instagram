package com.mininstagram.post;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain api(HttpSecurity http) throws Exception {
  return http.csrf(csrf->csrf.disable())
   .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/actuator/health/**","/actuator/prometheus").permitAll()
      .requestMatchers("/actuator/**").denyAll()
      .requestMatchers("/internal/**").access((auth,context) -> {
        boolean isService = auth.get() instanceof JwtAuthenticationToken token
           && "like-service".equals(token.getToken().getClaimAsString("azp"));
        return new AuthorizationDecision(isService);
      })
      .requestMatchers(HttpMethod.GET,"/api/v1/posts","/api/v1/posts/*").permitAll()
      .anyRequest().authenticated())
   .oauth2ResourceServer(o->o.jwt(withDefaults())).build();
 }
}
