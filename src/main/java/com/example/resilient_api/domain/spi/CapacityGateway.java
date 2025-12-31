package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.CapacityBootcampSaveResult;
import com.example.resilient_api.domain.model.CapacityTechnologies;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampCapacitiesResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityGateway {

    Mono<CapacityBootcampSaveResult> validateName(String name, String messageId);

    Mono<CapacityBootcampSaveResult> saveCapacities(Long idBootcamp, Bootcamp bootcamp, String messageId);

    Flux<BootcampCapacitiesResponse> getAllCapacities(String messageId);

    Flux<CapacityTechnologies> getCapacitiesByBootcamp(Long idBootcamp, String messageId);
}
