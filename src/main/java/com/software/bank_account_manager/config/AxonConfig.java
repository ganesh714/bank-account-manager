package com.software.bank_account_manager.config;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    // Requirement #11: Create a snapshot after every 5 events
    @Bean
    public SnapshotTriggerDefinition bankAccountSnapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public org.axonframework.eventsourcing.eventstore.EventStorageEngine storageEngine(
            org.axonframework.common.jpa.EntityManagerProvider entityManagerProvider,
            org.axonframework.common.transaction.TransactionManager transactionManager) {
        return org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine.builder()
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public org.axonframework.eventsourcing.eventstore.EmbeddedEventStore eventStore(
            org.axonframework.eventsourcing.eventstore.EventStorageEngine storageEngine,
            org.axonframework.spring.config.AxonConfiguration configuration) {
        return org.axonframework.eventsourcing.eventstore.EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .messageMonitor(configuration
                        .messageMonitor(org.axonframework.eventsourcing.eventstore.EventStore.class, "eventStore"))
                .build();
    }
}
