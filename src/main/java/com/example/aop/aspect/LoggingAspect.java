package com.example.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * ====================================================
 * [면접 포인트] AOP 5가지 Advice 타입 전부 포함
 * ====================================================
 * 1. @Before    - 메서드 실행 전
 * 2. @After     - 메서드 실행 후 (성공/실패 무관)
 * 3. @AfterReturning - 메서드 정상 리턴 후
 * 4. @AfterThrowing  - 메서드 예외 발생 후
 * 5. @Around    - 메서드 실행 전후 모두 제어 (가장 강력)
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // ── Pointcut 정의 ──────────────────────────────────
    // execution(접근제어자? 리턴타입 패키지.클래스.메서드(파라미터))
    @Pointcut("execution(* com.example.aop.service..*(..))")
    public void serviceLayer() {}

    // 커스텀 어노테이션 기반 Pointcut
    @Pointcut("@annotation(com.example.aop.annotation.LogExecution)")
    public void logExecutionAnnotation() {}

    // ── 1. @Before ─────────────────────────────────────
    @Before("serviceLayer()")
    public void beforeService(JoinPoint joinPoint) {
        log.info("[Before] {} | args: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // ── 2. @AfterReturning ─────────────────────────────
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void afterReturningService(JoinPoint joinPoint, Object result) {
        log.info("[AfterReturning] {} | result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    // ── 3. @AfterThrowing ──────────────────────────────
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void afterThrowingService(JoinPoint joinPoint, Exception ex) {
        log.error("[AfterThrowing] {} | exception: {}",
                joinPoint.getSignature().toShortString(),
                ex.getMessage());
    }

    // ── 4. @After ──────────────────────────────────────
    @After("serviceLayer()")
    public void afterService(JoinPoint joinPoint) {
        log.info("[After] {} (finally block과 유사 - 항상 실행)",
                joinPoint.getSignature().toShortString());
    }

    // ── 5. @Around (가장 많이 사용) ────────────────────
    @Around("logExecutionAnnotation()")
    public Object aroundLogExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("[Around-시작] {}", methodName);
        try {
            Object result = joinPoint.proceed(); // 실제 메서드 실행
            return result;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("[Around-종료] {} | 실행시간: {}ms", methodName, elapsed);
        }
    }
}
