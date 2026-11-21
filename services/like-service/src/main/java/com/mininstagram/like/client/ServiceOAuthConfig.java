package com.mininstagram.like.client;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import java.net.http.HttpClient;
import java.time.Duration;
@Configuration
public class ServiceOAuthConfig {
 @Bean OAuth2AuthorizedClientManager serviceAuthorizedClientManager(
     ClientRegistrationRepository registrations,OAuth2AuthorizedClientService clients) {
    var manager=new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations,clients);
    manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build());
    return manager;
 }
 @Bean RestClient postRestClient(RestClient.Builder builder,
      @org.springframework.beans.factory.annotation.Value("${post-service.base-url}") String baseUrl) {
    // Auto-configured builder propagates W3C trace context across service calls.
    var http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build();
    var factory = new JdkClientHttpRequestFactory(http);
    factory.setReadTimeout(Duration.ofSeconds(2));
    return builder.baseUrl(baseUrl).requestFactory(factory).build();
 }
}
