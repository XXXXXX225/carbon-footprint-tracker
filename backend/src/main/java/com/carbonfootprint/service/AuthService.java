package com.carbonfootprint.service;

import com.carbonfootprint.dto.AuthRequest;
import com.carbonfootprint.dto.AuthResponse;
import com.carbonfootprint.entity.User;
import com.carbonfootprint.repository.UserRepository;
import com.carbonfootprint.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(userDetails.getUsername());
        response.setName(((com.carbonfootprint.entity.User) userDetails).getName());
        response.setRole(role.replace("ROLE_", ""));
        
        return response;
    }
    
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        if (userRepository.existsByName(request.getName())) {
            throw new RuntimeException("姓名已被使用");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(User.Role.INDIVIDUAL);
        
        User savedUser = userRepository.save(user);
        
        String token = jwtUtil.generateToken(savedUser.getUsername(), "ROLE_" + savedUser.getRole().name());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(savedUser.getUsername());
        response.setName(savedUser.getName());
        response.setRole(savedUser.getRole().name());
        
        return response;
    }
}
