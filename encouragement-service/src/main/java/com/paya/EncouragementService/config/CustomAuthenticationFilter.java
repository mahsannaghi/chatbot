package com.paya.EncouragementService.config;

import com.paya.EncouragementService.entity.TblUser;
import com.paya.EncouragementService.enumeration.RoleConstant;
import com.paya.EncouragementService.service.AuthService;
import com.paya.EncouragementService.service.RoleMappingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final RoleMappingService roleMappingService;
    private final AuthService authService;

    public CustomAuthenticationFilter(RoleMappingService roleMappingService, AuthService authService) {
        this.roleMappingService = roleMappingService;
        this.authService = authService;
    }

    private Collection<GrantedAuthority> getAuthorities(Set<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                Set<String> roles = roleMappingService.determineRoles();
                TblUser user;
                try {
                    user = authService.getCurrentUserProfile();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (authentication.getAuthorities().toString().equals("[ROLE_ANONYMOUS]")) {
                    user.setCurrentRole(RoleConstant.ROLE.PERSONNEL.getValue());
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, null,
                                    getAuthorities(Set.of(RoleConstant.ROLE.PERSONNEL.name()))));
                } else {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, null,
                                    getAuthorities(Set.of(Objects.requireNonNull(RoleConstant.ROLE.fromValue(user.getCurrentRole()!=null?user.getCurrentRole():"0")).name()))));
                }
                user.setRoles(roles.stream().map(r -> RoleConstant.ROLE.valueOf(r).getValue()).collect(Collectors.toSet()));
            }
        }
        filterChain.doFilter(request, response);
    }
}


