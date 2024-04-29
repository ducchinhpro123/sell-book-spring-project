package org.project.sellbook.utils;

import org.project.sellbook.model.User;
import org.project.sellbook.security.CustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class UserUtil {
    private final CustomUserDetailsService customUserDetailsService;

    public UserUtil(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return customUserDetailsService.findUserByUserName(username);
    }
}
