# AOP 면접 대비 정리

## Q. AOP란?

> **AOP(Aspect-Oriented Programming)** 는 **횡단 관심사(Cross-Cutting Concern)** 를 모듈화하는 프로그래밍 패러다임입니다.

비즈니스 로직과 직접 관련 없지만 여러 곳에 반복되는 코드(로깅, 트랜잭션, 인증 등)를
**핵심 로직에서 분리**하여 한 곳에서 관리합니다.

### 핵심 개념 (용어)

| 용어 | 설명 | 코드 예시 |
|------|------|-----------|
| **Aspect** | 횡단 관심사를 모듈화한 클래스 | `@Aspect` 클래스 |
| **Advice** | 실제 실행되는 부가 기능 | `@Before`, `@Around` 메서드 |
| **JoinPoint** | Advice가 적용될 수 있는 지점 | 메서드 실행 시점 |
| **Pointcut** | 어떤 JoinPoint에 적용할지 정의 | `execution(* com.example..*(..))` |
| **Target** | Advice가 적용되는 대상 객체 | Service 클래스 |
| **Proxy** | AOP가 적용된 프록시 객체 | Spring이 자동 생성 |

### 5가지 Advice 타입

```
@Before         → 메서드 실행 전
@AfterReturning → 메서드 정상 리턴 후
@AfterThrowing  → 메서드 예외 발생 후
@After          → 메서드 실행 후 (finally와 유사, 항상 실행)
@Around         → 메서드 실행 전후 모두 제어 (가장 강력, 가장 많이 사용)
```

### 실행 순서

```
@Around (시작)
  └─ @Before
      └─ 실제 메서드 실행
  └─ @AfterReturning (정상) 또는 @AfterThrowing (예외)
  └─ @After (항상)
@Around (종료)
```

---

## Q. AOP 활용 방법은?

### 실무에서 주로 사용하는 곳

1. **로깅** - API 호출 로그, 메서드 실행시간 측정
2. **트랜잭션 관리** - `@Transactional` 자체가 AOP로 동작
3. **인증/인가** - 권한 체크를 Aspect로 분리
4. **예외 처리** - 공통 예외 로깅
5. **캐싱** - `@Cacheable`도 AOP 기반

### 이 프로젝트의 예시

```java
// 1. 커스텀 어노테이션 정의
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {}

// 2. Aspect에서 해당 어노테이션이 붙은 메서드에 적용
@Around("@annotation(com.example.aop.annotation.LogExecution)")
public Object aroundLogExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed(); // 실제 메서드 실행
    log.info("실행시간: {}ms", System.currentTimeMillis() - start);
    return result;
}

// 3. Service에서 어노테이션만 붙이면 끝
@LogExecution("사용자 조회")
public UserDto getUser(Long id) { ... }
```

---

## Q. Spring AOP vs AspectJ 차이는?

| 항목 | Spring AOP | AspectJ |
|------|-----------|---------|
| 방식 | **프록시 기반** (런타임) | 바이트코드 조작 (컴파일/로드타임) |
| 적용 대상 | Spring Bean의 메서드만 | 모든 Java 객체 |
| JoinPoint | 메서드 실행만 지원 | 필드 접근, 생성자 등도 지원 |
| 성능 | 약간 느림 (프록시 경유) | 빠름 |
| 실무 | **대부분 이걸로 충분** | 특수한 경우에만 사용 |

---

## Q. AOP 동작 원리 (프록시)

```
Client → Proxy(AOP 적용) → Target(실제 객체)
```

- Spring은 **JDK Dynamic Proxy** (인터페이스 기반) 또는 **CGLIB Proxy** (클래스 상속 기반) 사용
- Spring Boot 기본값: **CGLIB**
- `@Transactional`이 같은 클래스 내부 호출(self-invocation)에서 안 먹는 이유가 바로 **프록시를 거치지 않기 때문**

### 주의사항 (면접 단골)

> **self-invocation 문제**: 같은 클래스 내에서 `this.method()` 호출 시 프록시를 거치지 않아 AOP가 적용되지 않음

```java
@Service
public class OrderService {
    @Transactional
    public void createOrder() { ... }

    public void process() {
        this.createOrder(); // ❌ AOP 미적용 (프록시 우회)
    }
}
```

---

## 테스트 방법

```bash
# 실행
./gradlew bootRun

# API 호출
curl localhost:8080/api/users/1       # Before, AfterReturning, After, Around 로그 확인
curl localhost:8080/api/users          # Before, AfterReturning, After 로그 확인
curl localhost:8080/api/users/error    # AfterThrowing 로그 확인
```
