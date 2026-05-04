package com.carbonfootprint.security;

import com.carbonfootprint.service.UserDetailsServiceImpl;
import com.carbonfootprint.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        
        // 1. 如果没有 Token，或者格式不对，直接放行，交给 SecurityConfig 去判断权限
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("[JWT Filter] 放行无Token请求: " + request.getRequestURI());
            chain.doFilter(request, response); // 修复了这里的变量名，使用 chain
            return; // 必须 return，否则会继续往下走
        }
        
        // 2. 如果有 Token，则提取并验证
        String token = authorizationHeader.substring(7);
        System.out.println("[JWT Filter] 开始验证Token, 路径: " + request.getRequestURI());
        
        try {
            String username = jwtUtil.extractUsername(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 验证通过，把用户信息存入上下文
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("[JWT Filter] 认证成功，用户: " + username);
                } else {
                    System.out.println("[JWT Filter] Token验证失败 (可能已过期)");
                }
            }
        } catch (Exception e) {
            System.out.println("[JWT Filter] Token处理异常: " + e.getMessage());
        }
        
        // 3. 验证完毕，继续执行后续的过滤器链
        chain.doFilter(request, response);
    }
}