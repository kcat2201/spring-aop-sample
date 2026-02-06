package com.example.aop.controller;

import com.example.aop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * self-invocation 문제 테스트용 Controller
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 외부에서 직접 호출 → AOP 정상 적용
     * 로그: [Around-시작], [Around-종료] 출력됨
     */
    @GetMapping("/create")
    public ResponseEntity<String> createOrder() {
        orderService.createOrder();
        return ResponseEntity.ok("AOP 적용됨 - 로그 확인");
    }

    /**
     * self-invocation 문제 발생
     * processWithSelfInvocation() 내부에서 this.createOrder() 호출
     * → createOrder()의 @LogExecution AOP가 적용되지 않음
     */
    @GetMapping("/self-invocation")
    public ResponseEntity<String> selfInvocationTest() {
        orderService.processWithSelfInvocation();
        return ResponseEntity.ok("self-invocation 발생 - AOP 로그 없음 확인");
    }
}
