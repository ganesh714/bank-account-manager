package com.software.bank_account_manager.command.controllers;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	
	@Autowired
	EventProcessingConfiguration configuration;
	
	@PostMapping("/replay/{processingGroup}")
    public ResponseEntity<String> replayEvents(@PathVariable String processingGroup) {
        configuration.eventProcessor(processingGroup, TrackingEventProcessor.class)
                .ifPresent(processor -> {
                    processor.shutDown();
                    processor.resetTokens(); // This forces it to start reading from the beginning of time
                    processor.start();
                });
        return ResponseEntity.ok("Replay initiated for processing group: " + processingGroup);
    }
}
