package com.example.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * [면접 포인트] 실무 활용 예시 - API 실행시간 측정
 */
@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("execution(* com.example.aop.controller..*(..))")
    public Object measureApiPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String api = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > 1000) {
                log.warn("[SLOW API] {} - {}ms", api, elapsed);
            } else {
                log.info("[API] {} - {}ms", api, elapsed);
            }
        }
    }
}
