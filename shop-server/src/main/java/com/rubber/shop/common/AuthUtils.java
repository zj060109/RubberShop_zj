package com.rubber.shop.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("未登录");
        }
        return (Long) auth.getPrincipal();
    }

    public static boolean isMerchant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_MERCHANT".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    public static boolean isFactory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_FACTORY".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    public static void requireMerchant() {
        if (!isMerchant()) throw new BusinessException("权限不足，仅商户可操作");
    }
}
