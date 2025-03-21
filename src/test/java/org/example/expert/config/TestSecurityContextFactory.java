package org.example.expert.config;

import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockAuthUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AuthUser authUser = new AuthUser(annotation.userId(), annotation.email(), annotation.nickname(), annotation.role());
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);
        context.setAuthentication(authentication);
        return context;
    }
}
