package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户个人信息相关接口")
public class UserController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的个人信息")
    public ResponseEntity<ApiResult<User>> getUserInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResult.success(user));
    }
    
    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    public ResponseEntity<ApiResult<User>> updateUserInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateRequest request) {
        
        // 更新用户信息
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        // 保存更新后的用户信息
        User updatedUser = userRepository.save(user);
        
        return ResponseEntity.ok(ApiResult.success(updatedUser));
    }
    
    // 用户信息更新请求DTO
    public static class UserUpdateRequest {
        private String name;
        private String email;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }
}
