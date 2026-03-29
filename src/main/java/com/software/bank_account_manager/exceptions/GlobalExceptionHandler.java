package com.software.bank_account_manager.exceptions;

import java.util.Map;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(AggregateNotFoundException .class)
	public ResponseEntity<Object> handleAggregateNotFound(AggregateNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Account not found", "details", ex.getMessage()));
    }
	
	@ExceptionHandler(CommandExecutionException.class)
	public ResponseEntity<Object> handleCommandExecutionException(CommandExecutionException ex) {
        Throwable cause = ex.getCause();
        
        if (cause instanceof IllegalStateException || cause instanceof IllegalArgumentException) {
            // This catches "Insufficient funds", "Cannot close account with balance", etc.
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Business Rule Violation", "details", cause.getMessage()));
        }
        
        // Fallback for other errors
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error", "details", ex.getMessage()));
    }
}
