package com.carbonfootprint.controller;

import com.carbonfootprint.dto.ApiResult;
import com.carbonfootprint.dto.UserRegistrationStatsDTO;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理员接口", description = "管理员查看用户注册情况等相关接口")
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有用户列表", description = "获取系统中所有注册用户的信息列表")
    public ResponseEntity<ApiResult<List<UserInfoDTO>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserInfoDTO> userInfoList = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResult.success(userInfoList));
    }

    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户注册统计", description = "获取用户注册情况的统计数据，包括总数、今日新增、本周新增等")
    public ResponseEntity<ApiResult<UserRegistrationStatsDTO>> getUserRegistrationStats() {
        List<User> allUsers = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = todayStart.minusDays(todayStart.getDayOfWeek().getValue() - 1);
        LocalDateTime monthStart = todayStart.withDayOfMonth(1);

        long totalUsers = allUsers.size();
        long todayNewUsers = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(todayStart))
                .count();
        long weekNewUsers = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(weekStart))
                .count();
        long monthNewUsers = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(monthStart))
                .count();

        Map<String, Long> roleDistribution = allUsers.stream()
                .collect(Collectors.groupingBy(u -> u.getRole().name(), Collectors.counting()));

        Map<String, Long> dailyRegistrationTrend = getDailyRegistrationTrend(allUsers);

        UserRegistrationStatsDTO stats = new UserRegistrationStatsDTO();
        stats.setTotalUsers(totalUsers);
        stats.setTodayNewUsers(todayNewUsers);
        stats.setWeekNewUsers(weekNewUsers);
        stats.setMonthNewUsers(monthNewUsers);
        stats.setRoleDistribution(roleDistribution);
        stats.setDailyRegistrationTrend(dailyRegistrationTrend);

        return ResponseEntity.ok(ApiResult.success(stats));
    }

    private Map<String, Long> getDailyRegistrationTrend(List<User> users) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7).toLocalDate().atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        return users.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(sevenDaysAgo))
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().format(formatter),
                        TreeMap::new,
                        Collectors.counting()));
    }

    private UserInfoDTO convertToDTO(User user) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setTotalPoints(user.getTotalPoints());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "编辑用户信息", description = "根据用户ID编辑用户信息，包括角色、姓名、邮箱等")
    public ResponseEntity<ApiResult<UserInfoDTO>> updateUser(@PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.ok(ApiResult.error("用户不存在"));
        }

        User user = userOptional.get();
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            try {
                user.setRole(User.Role.valueOf(request.getRole()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(ApiResult.error("无效的角色类型"));
            }
        }
        if (request.getTotalPoints() != null) {
            user.setTotalPoints(request.getTotalPoints());
        }

        userRepository.save(user);
        return ResponseEntity.ok(ApiResult.success(convertToDTO(user)));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public ResponseEntity<ApiResult<String>> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.ok(ApiResult.error("用户不存在"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResult.success("用户删除成功"));
    }

    public static class UserInfoDTO {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String role;
        private Integer totalPoints;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Integer getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(Integer totalPoints) {
            this.totalPoints = totalPoints;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class UpdateUserRequest {
        private String name;
        private String email;
        private String role;
        private Integer totalPoints;

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Integer getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(Integer totalPoints) {
            this.totalPoints = totalPoints;
        }
    }
}
