package com.carbonfootprint.config;

import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        updateRoleColumn();
        ensureAdminUser();
    }

    private void updateRoleColumn() {
        try {
            jdbcTemplate
                    .execute("ALTER TABLE users MODIFY COLUMN role ENUM('INDIVIDUAL', 'ENTERPRISE', 'ADMIN') NOT NULL");
            log.info("成功更新 users 表的 role 列");
        } catch (Exception e) {
            log.info("role 列已存在或更新失败: {}", e.getMessage());
        }
    }

    private void ensureAdminUser() {
        User rootUser = userRepository.findByUsername("root").orElse(null);

        if (rootUser == null) {
            rootUser = new User();
            rootUser.setUsername("root");
            rootUser.setPassword(passwordEncoder.encode("123456"));
            rootUser.setName("超级管理员");
            rootUser.setEmail("root@example.com");
            rootUser.setRole(User.Role.ADMIN);
            rootUser.setTotalPoints(0);
            userRepository.save(rootUser);
            log.info("创建管理员账号: root/123456");
        } else if (rootUser.getRole() != User.Role.ADMIN) {
            rootUser.setRole(User.Role.ADMIN);
            userRepository.save(rootUser);
            log.info("更新 root 用户角色为 ADMIN");
        } else {
            log.info("管理员账号已存在且角色正确");
        }
    }
}
