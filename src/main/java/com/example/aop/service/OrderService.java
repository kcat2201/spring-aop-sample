package com.example.aop.service;

import com.example.aop.annotation.LogExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ====================================================
 * self-invocation 문제 데모
 * ====================================================
 * 같은 클래스 내에서 this.method() 호출 시
 * 프록시를 거치지 않아 AOP가 적용되지 않는 문제
 */
@Service
@Slf4j
public class OrderService {

    @LogExecution("주문 생성")
    public void createOrder() {
        log.info("주문이 생성되었습니다.");
    }

    /**
     * self-invocation 문제 발생!
     * this.createOrder() 호출 시 프록시를 거치지 않음
     * → @LogExecution AOP가 적용되지 않음
     */
    public void processWithSelfInvocation() {
        log.info("processWithSelfInvocation 시작");
        this.createOrder();  // ❌ AOP 미적용 (프록시 우회)
        log.info("processWithSelfInvocation 종료");
    }

    /**
     * 외부에서 직접 호출하면 AOP 정상 적용
     */
    public void processNormal() {
        log.info("processNormal - 이 메서드 자체는 AOP 없음");
    }
}
