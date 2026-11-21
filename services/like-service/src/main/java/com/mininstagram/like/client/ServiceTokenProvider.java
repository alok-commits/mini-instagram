package com.mininstagram.like.client;
import com.mininstagram.common.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.*;
import org.springframework.stereotype.Component;
@Component
public class ServiceTokenProvider {
 private final OAuth2AuthorizedClientManager manager;
 public ServiceTokenProvider(OAuth2AuthorizedClientManager manager){this.manager=manager;}
 public String accessToken(){
    var request=OAuth2AuthorizeRequest.withClientRegistrationId("like-service")
        .principal("like-service-system").build();
    var client=manager.authorize(request);
    if(client==null||client.getAccessToken()==null)
      throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE,"SERVICE_AUTH_UNAVAILABLE",
      "Internal service authorization unavailable");
    return client.getAccessToken().getTokenValue();
 }
}
