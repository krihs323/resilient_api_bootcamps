package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.CapacityBootcampSaveResult;
import reactor.core.publisher.Mono;

public interface CapacityGateway {

    Mono<CapacityBootcampSaveResult> validateName(String name, String messageId);

    Mono<CapacityBootcampSaveResult> saveCapacities(Long idBootcamp, Bootcamp bootcamp, String messageId);
}
