package com.example.proyecto_logitrack.exception;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int status, String message, String errorCode) {
}