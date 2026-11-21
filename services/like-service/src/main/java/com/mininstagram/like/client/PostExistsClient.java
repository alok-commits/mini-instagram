package com.mininstagram.like.client;
import com.mininstagram.common.BusinessException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import java.util.UUID;
/** Converts internal transport outcomes into safe API errors after resilience advice executes. */
@Component
public class PostExistsClient {
 private final PostAvailabilityTransport transport;
 public PostExistsClient(PostAvailabilityTransport transport){this.transport=transport;}
 public void assertExists(UUID id){
   try { transport.verify(id); }
   catch (HttpClientErrorException.NotFound ex) {
      throw new BusinessException(HttpStatus.NOT_FOUND,"POST_NOT_FOUND","Post not found");
   } catch (CallNotPermittedException ex) {
      throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE,"POST_CIRCUIT_OPEN","Post service is temporarily unavailable");
   } catch (BusinessException ex) {
      throw ex;
   } catch (RestClientException ex) {
      throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE,"POST_SERVICE_UNAVAILABLE","Post verification unavailable");
   }
 }
}
