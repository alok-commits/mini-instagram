package com.mininstagram.like.client;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.UUID;
/** Separate proxied bean so @Retry and @CircuitBreaker can intercept actual network failures. */
@Component
public class PostAvailabilityTransport {
 private final RestClient client;
 private final ServiceTokenProvider tokens;
 public PostAvailabilityTransport(RestClient postRestClient, ServiceTokenProvider tokens) {
   this.client=postRestClient;this.tokens=tokens;
 }
 @Retry(name="postService") @CircuitBreaker(name="postService")
 public void verify(UUID id) {
   client.get().uri("/internal/posts/{id}/exists",id)
     .headers(h->h.setBearerAuth(tokens.accessToken()))
     .retrieve().toBodilessEntity();
 }
}
