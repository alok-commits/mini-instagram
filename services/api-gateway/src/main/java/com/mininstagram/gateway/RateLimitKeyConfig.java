package com.mininstagram.gateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import java.net.InetSocketAddress;
@Configuration
public class RateLimitKeyConfig {
 @Bean("principalOrIpKeyResolver")
 KeyResolver keyResolver(@Value("${gateway.trust-proxy-header:false}") boolean trustProxy){
   return exchange -> exchange.getPrincipal()
     .filter(principal -> principal instanceof JwtAuthenticationToken)
     .map(principal -> "subject:"+((JwtAuthenticationToken)principal).getToken().getSubject())
     .switchIfEmpty(Mono.fromSupplier(()->{
        String trustedIp=trustProxy?exchange.getRequest().getHeaders().getFirst("X-Client-IP"):null;
        if(trustedIp!=null&&trustedIp.matches("[0-9a-fA-F:.]{3,45}")) return "ip:"+trustedIp;
        InetSocketAddress remote=exchange.getRequest().getRemoteAddress();
        return "ip:"+(remote==null?"unknown":remote.getHostString());
     }));
 }
}
