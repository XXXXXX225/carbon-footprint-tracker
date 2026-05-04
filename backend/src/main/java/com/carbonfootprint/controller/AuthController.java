package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.dto.AuthRequest;
import com.carbonfootprint.dto.AuthResponse;
import com.carbonfootprint.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth", "/auth"})
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码登录，获取JWT令牌")
    public ResponseEntity<ApiResult<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            System.out.println("[Debug] 收到登录请求, 用户名: " + request.getUsername());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResult.success(response));
        } catch (Exception e) {
            // 强行把报错信息和红字堆栈打印到 Docker 日志里
            System.err.println("[Critical Error] 登录业务执行崩溃: " + e.getMessage());
            e.printStackTrace(); 
            throw e; 
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ResponseEntity<ApiResult<AuthResponse>> register(@RequestBody AuthRequest request) {
        try {
            System.out.println("[Debug] 收到注册请求, 用户名: " + request.getUsername());
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResult.success(response));
        } catch (Exception e) {
            // 同样强行捕获并打印注册时的异常
            System.err.println("[Critical Error] 注册业务执行崩溃: " + e.getMessage());
            e.printStackTrace(); 
            throw e; 
        }
    }
}