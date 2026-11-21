package com.mininstagram.common;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/** Logs operation names, outcomes and durations; never method arguments or secrets. */
@Aspect @Component
public class OperationLoggingAspect {
  private static final Logger log = LoggerFactory.getLogger(OperationLoggingAspect.class);
  @Around("@annotation(annotation)")
  public Object around(ProceedingJoinPoint joinPoint, OperationLogged annotation) throws Throwable {
    long started = System.nanoTime();
    String outcome = "SUCCESS";
    try { return joinPoint.proceed(); }
    catch (Throwable error) { outcome = "FAILURE:" + error.getClass().getSimpleName(); throw error; }
    finally {
      log.info("operation={} outcome={} durationMs={}", annotation.value(), outcome,
          TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-started));
    }
  }
}
