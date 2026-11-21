package com.mininstagram.common;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.dao.OptimisticLockingFailureException;

/** Unified RFC 9457 problem responses. Avoids leaking internals and personal data. */
@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger log=LoggerFactory.getLogger(ApiExceptionHandler.class);
  private ResponseEntity<ProblemDetail> problem(HttpStatus status, String code, String detail) {
    ProblemDetail body=ProblemDetail.forStatusAndDetail(status, detail);
    body.setTitle(status.getReasonPhrase()); body.setProperty("errorCode",code);
    String traceId=MDC.get("traceId"); if(traceId!=null) body.setProperty("traceId",traceId);
    return ResponseEntity.status(status).body(body);
  }
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetail> business(BusinessException ex) {
    return problem(ex.status(),ex.errorCode(),ex.getMessage());
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException ex) {
    return problem(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","One or more request fields are invalid");
  }
  @ExceptionHandler({MethodArgumentTypeMismatchException.class,IllegalArgumentException.class})
  public ResponseEntity<ProblemDetail> malformed(Exception ex) {
    return problem(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","Invalid parameter or request value");
  }
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> conflict(DataIntegrityViolationException ex) {
    return problem(HttpStatus.CONFLICT,"DATA_CONFLICT","Conflicting or duplicate resource");
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> badJson(HttpMessageNotReadableException ex) {
    return problem(HttpStatus.BAD_REQUEST,"MALFORMED_JSON","Request JSON is malformed");
  }
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> wrongMethod(HttpRequestMethodNotSupportedException ex) {
    return problem(HttpStatus.METHOD_NOT_ALLOWED,"METHOD_NOT_ALLOWED","HTTP method is not supported");
  }
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> wrongMedia(HttpMediaTypeNotSupportedException ex) {
    return problem(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"UNSUPPORTED_MEDIA_TYPE","Unsupported content type");
  }
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> missingRoute(NoResourceFoundException ex) {
    return problem(HttpStatus.NOT_FOUND,"ROUTE_NOT_FOUND","Resource not found");
  }
  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<ProblemDetail> concurrentUpdate(OptimisticLockingFailureException ex) {
    return problem(HttpStatus.CONFLICT,"CONCURRENT_UPDATE","Resource was modified concurrently");
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> unexpected(Exception ex) {
    log.error("Unhandled request failure",ex);
    return problem(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_ERROR","An internal error occurred");
  }
}
