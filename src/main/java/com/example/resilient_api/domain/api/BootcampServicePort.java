package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.CapacityTechnologies;
import com.example.resilient_api.domain.model.PageResponse;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {
    Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp, String messageId);
    Mono<PageResponse<BootcampCapacitiesReportDto>> listBootcampsPage(int page, int size, String sortBy, String sortDir, String messageId);
    Flux<CapacityTechnologies> listCapacitiesByBootcamp(Long idBootcamp, String messageId);
    Mono<Void> deleteBootcamp(Long id, String messageId);
    Mono<Bootcamp> getBootcamp(Long idBootcamp, String messageId);
}
