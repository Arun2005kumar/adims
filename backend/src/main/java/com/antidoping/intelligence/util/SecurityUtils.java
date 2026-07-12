package com.antidoping.intelligence.util;

import com.antidoping.intelligence.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal;
    }

    public static Long getCurrentUserId() {
        UserPrincipal principal = getCurrentUser();
        return principal == null ? null : principal.getId();
    }
}
