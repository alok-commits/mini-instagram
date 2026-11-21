package com.mininstagram.user;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain api(HttpSecurity http) throws Exception {
  return http.csrf(csrf->csrf.disable())
    .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(a->a.requestMatchers("/actuator/health/**","/actuator/prometheus").permitAll()
       .requestMatchers("/actuator/**").denyAll()
       .anyRequest().authenticated())
    .oauth2ResourceServer(o->o.jwt(withDefaults())).build();
 }
}
