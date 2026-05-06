package com.software.bank_account_manager.config;

import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AxonConfig {

    // Requirement #11: Create a snapshot after every 5 events
    @Bean
    public SnapshotTriggerDefinition bankAccountSnapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public org.axonframework.common.jpa.EntityManagerProvider entityManagerProvider(jakarta.persistence.EntityManager entityManager) {
        return new org.axonframework.common.jpa.SimpleEntityManagerProvider(entityManager);
    }

    @Bean
    public TransactionManager axonTransactionManager(PlatformTransactionManager platformTransactionManager) {
        return new SpringTransactionManager(platformTransactionManager);
    }

    @Bean
    public org.axonframework.eventsourcing.eventstore.EventStorageEngine storageEngine(
            org.axonframework.common.jpa.EntityManagerProvider entityManagerProvider,
            org.axonframework.common.transaction.TransactionManager transactionManager) {
        
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        // Required for Axon to correctly serialize class metadata
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

        org.axonframework.serialization.Serializer jacksonSerializer = 
            org.axonframework.serialization.json.JacksonSerializer.builder()
                .objectMapper(objectMapper)
                .build();

        return org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine.builder()
                .snapshotSerializer(jacksonSerializer)
                .eventSerializer(jacksonSerializer)
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public org.axonframework.eventsourcing.eventstore.EmbeddedEventStore eventStore(
            org.axonframework.eventsourcing.eventstore.EventStorageEngine storageEngine) {
        return org.axonframework.eventsourcing.eventstore.EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .build();
    }
}
