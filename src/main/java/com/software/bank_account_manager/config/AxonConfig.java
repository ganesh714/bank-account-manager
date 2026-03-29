package com.software.bank_account_manager.config;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {
	
	@Bean
	public SnapshotTriggerDefinition bankAccountSnapshotTriggerDefinition(Snapshotter snapshotter) {
        // Requirement #11: Create a snapshot after every 5 events
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }
}
