package org.motechproject.ananya.kilkari.web;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;

@Aspect
@Component
public class GatewayLogger {
    private final Logger logger = LoggerFactory.getLogger("RequestResponseLogger");

    @Pointcut("execution(public * org.springframework.web.client.RestTemplate.*(..))")
    public void allExternalHttpCalls() {
    }

    @Before("allExternalHttpCalls()")
    public void beforeRESTCall(JoinPoint joinPoint) {
        if (logger.isDebugEnabled()) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                logger.debug("Accessing external url: {}", Arrays.toString(args));
            }
        }
    }

    @AfterThrowing(value = "allExternalHttpCalls()", throwing = "e")
    public void onRestCallException(JoinPoint joinPoint, HttpServerErrorException e) {
        logger.error(e.getResponseBodyAsString());
    }

    @AfterReturning(pointcut = "allExternalHttpCalls()", returning = "result")
    public void afterRESTCall(JoinPoint joinPoint, Object result) {
        if (logger.isDebugEnabled()) {
            Object[] args = joinPoint.getArgs();
            String arguments = "";
            if (args.length > 0) {
                arguments = Arrays.toString(args);
            }
            logger.debug("After accessing external url: {} got response: {}", arguments, result);
        }
    }
}
