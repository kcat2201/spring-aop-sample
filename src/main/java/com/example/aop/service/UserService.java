package com.example.aop.service;

import com.example.aop.annotation.LogExecution;
import com.example.aop.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @LogExecution("사용자 조회")
    public UserDto getUser(Long id) {
        return new UserDto(id, "홍길동", "hong@example.com");
    }

    public List<UserDto> getAllUsers() {
        return List.of(
                new UserDto(1L, "홍길동", "hong@example.com"),
                new UserDto(2L, "김철수", "kim@example.com")
        );
    }

    @LogExecution("사용자 생성")
    public UserDto createUser(String name, String email) {
        return new UserDto(3L, name, email);
    }

    public void failingMethod() {
        throw new RuntimeException("의도적 예외 - AfterThrowing 테스트");
    }
}
