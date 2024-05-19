package com.xxavierr404.auth.controller;

import com.xxavierr404.auth.dto.UserInfoResponseDto;
import com.xxavierr404.auth.dto.UserRegisterDto;
import com.xxavierr404.auth.jwt.JwtRequest;
import com.xxavierr404.auth.jwt.JwtResponse;
import com.xxavierr404.auth.service.AuthService;
import com.xxavierr404.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody UserRegisterDto userRegisterDto
    ) {
        userService.register(userRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user-info/{id}")
    public ResponseEntity<UserInfoResponseDto> checkExistence(
            @PathVariable Long id
    ) {
        var user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return ResponseEntity.ok(
                new UserInfoResponseDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getOrganizationId(),
                        user.getRole()
                )
        );
    }

    @GetMapping("/user-infos/organization/{id}")
    public ResponseEntity<List<UserInfoResponseDto>> getAllByCompanyId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                userService.findAllByOrganizationId(id).stream()
                        .map(it -> new UserInfoResponseDto(
                                it.getId(),
                                it.getFirstName(),
                                it.getLastName(),
                                it.getOrganizationId(),
                                it.getRole()
                        ))
                        .toList()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> errorHandler(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<String> errorHandler(IllegalStateException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }
}