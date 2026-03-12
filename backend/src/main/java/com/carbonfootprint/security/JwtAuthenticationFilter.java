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
        
        System.out.println("[JWT Filter] 请求路径: " + request.getRequestURI());
        System.out.println("[JWT Filter] Authorization头: " + (authorizationHeader != null ? authorizationHeader.substring(0, Math.min(20, authorizationHeader.length())) + "..." : "null"));
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            System.out.println("[JWT Filter] Token长度: " + token.length());
            
            try {
                String username = jwtUtil.extractUsername(token);
                System.out.println("[JWT Filter] 提取的用户名: " + username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("[JWT Filter] 用户详情: " + userDetails.getUsername());
                    
                    if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        System.out.println("[JWT Filter] 认证成功，用户: " + username);
                    } else {
                        System.out.println("[JWT Filter] Token验证失败");
                    }
                }
            } catch (Exception e) {
                System.out.println("[JWT Filter] Token处理异常: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[JWT Filter] 没有有效的Authorization头");
        }
        
        chain.doFilter(request, response);
    }
}
