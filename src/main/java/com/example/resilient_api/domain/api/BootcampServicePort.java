package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.BootcampList;
import com.example.resilient_api.domain.model.PageResponse;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {
    Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp, String messageId);

    Mono<PageResponse<BootcampCapacitiesReportDto>> listBootcampsPage(int page, int size, String sortBy, String sortDir, String messageId);

}
