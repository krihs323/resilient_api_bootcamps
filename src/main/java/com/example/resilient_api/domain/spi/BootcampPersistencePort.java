package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.BootcampList;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Boolean> existByName(String name);
    Flux<BootcampCapacitiesReportDto> listCapacitiesPage(int page, int size, String sortBy, String sortDir, String messageId);
    Mono<Long> countGroupedCapacities();
    Flux<BootcampList> findCapabilitiesOrderedByName(int page, int size, String sortBy, String sortDir, String messageId);

}
