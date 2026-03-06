package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "测试接口", description = "用于调试的测试接口")
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    @Operation(summary = "获取所有用户（测试用）", description = "获取数据库中所有用户信息")
    public ApiResult<List<User>> getAllUsers() {
        return ApiResult.success(userRepository.findAll());
    }

    @PostMapping("/update-root-role")
    @Operation(summary = "更新root用户角色", description = "将root用户的角色更新为ADMIN")
    public ApiResult<String> updateRootRole() {
        User rootUser = userRepository.findByUsername("root").orElse(null);
        if (rootUser == null) {
            return ApiResult.error("root用户不存在");
        }
        rootUser.setRole(User.Role.ADMIN);
        userRepository.save(rootUser);
        return ApiResult.success("root用户角色已更新为ADMIN");
    }
}
