package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Boolean> existByName(String name);
    Flux<Bootcamp> listBootcampsPage(int page, int size, String sortBy, String sortDir, String messageId);
    Mono<Long> countBootcamps();

}
