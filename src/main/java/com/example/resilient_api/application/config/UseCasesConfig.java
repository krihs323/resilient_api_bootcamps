package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.spi.CapacityGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.usecase.BootcampUseCase;
import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.BootcampPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampCapacitiesEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampCapacitiesRepository;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final BootcampRepository bootcampRepository;
        private final BootcampEntityMapper bootcampEntityMapper;

        private final BootcampCapacitiesRepository bootcampCapacitiesRepository;
        private final BootcampCapacitiesEntityMapper bootcampCapacitiesEntityMapper;

        private final BootcampListMapper bootcampListMapper;

        private final DatabaseClient databaseClient;

        @Bean
        public BootcampPersistencePort capacitiesPersistencePort() {
                return new BootcampPersistenceAdapter(bootcampRepository, bootcampEntityMapper, bootcampCapacitiesRepository, bootcampCapacitiesEntityMapper, bootcampListMapper, databaseClient);
        }

        @Bean
        public BootcampServicePort capacitiesServicePort(BootcampPersistencePort capacitiesPersistencePort, CapacityGateway capacityGateway){
                return new BootcampUseCase(capacitiesPersistencePort, capacityGateway);
        }
}
