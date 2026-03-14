package com.example.proyecto_logitrack.config;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getUsuarioActual() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}