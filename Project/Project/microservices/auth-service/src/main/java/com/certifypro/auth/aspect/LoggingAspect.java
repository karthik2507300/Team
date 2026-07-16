package com.certifypro.auth.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cross-cutting logger for every controller and service method.
 * Emits INFO entry/exit + timing and ERROR on exception. All output lands in
 * {@code logs/spring.log} (configured via logging.file.name in the config server).
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.certifypro.auth.controller..*)")
    public void controllerLayer() {
    }

    @Pointcut("within(com.certifypro.auth.service..*)")
    public void serviceLayer() {
    }

    @Around("controllerLayer() || serviceLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String target = pjp.getSignature().getDeclaringType().getSimpleName()
                + "." + pjp.getSignature().getName() + "()";
        long start = System.currentTimeMillis();
        log.info("ENTER {} args={}", target, argsToString(pjp.getArgs()));
        try {
            Object result = pjp.proceed();
            log.info("EXIT  {} ({} ms)", target, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("ERROR {} ({} ms): {}", target, System.currentTimeMillis() - start, ex.getMessage());
            throw ex;
        }
    }

    private String argsToString(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            String s = a == null ? "null" : a.getClass().getSimpleName();
            // Never log credentials / token payloads.
            if (s.contains("Login") || s.contains("Register") || s.contains("Token")
                    || s.contains("Staff") || s.contains("Password")) {
                sb.append(s).append("(masked)");
            } else {
                sb.append(a);
            }
            if (i < args.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }
}
