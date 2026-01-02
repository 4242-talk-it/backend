package com.talkit.app.global.auth;

import com.talkit.app.domain.user.service.UserDetailsImpl;
import com.talkit.app.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationHolder {

    private AuthenticationHolder() { }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return User.ANONYMOUS_USER_ID;
    }

}
