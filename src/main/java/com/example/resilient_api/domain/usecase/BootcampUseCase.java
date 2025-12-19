package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.CapacityGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.infrastructure.adapters.emailvalidatoradapter.dto.BootcampCapacitiesResponse;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BootcampUseCase implements BootcampServicePort {

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacityGateway capacityGateway;


    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort, CapacityGateway capacityGateway) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacityGateway = capacityGateway;
    }

    @Override
    public Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp, String messageId) {
        return bootcampPersistencePort.existByName(bootcamp.name())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_ALREADY_EXISTS)))
                .flatMap(exists -> validateDuplicate(bootcamp.bootcampCapacityList()))
                .flatMap(x-> bootcampPersistencePort.save(bootcamp))
                .flatMap(savedBootcamp -> saveCapacities(savedBootcamp.id(), bootcamp, messageId)
                        .then(Mono.just(savedBootcamp))
                );
    }


    @Override
    public Mono<PageResponse<BootcampCapacitiesReportDto>> listBootcampsPage(int page, int size, String sortBy, String sortDir, String messageId) {
        Mono<Long> total = bootcampPersistencePort.countBootcamps();

        //Obtenemos los bootcamps como una lista
        Mono<List<Bootcamp>> bootcampsMono = bootcampPersistencePort.listBootcampsPage(page, size, sortBy, sortDir, messageId)
                .collectList()
                .doOnNext(list -> {
                    log.info("DEBUG - Lista de Bootcamps recuperada:");
                    list.forEach(bootcamp -> log.info(" > " + bootcamp));
                    log.info("Total elementos en página: " + list.size());
                });

        //Obtenemos todas las capacidades y las agrupamos por idBootcamp en un Map
        // Esto optimiza la búsqueda: K = idBootcamp, V = cantidad de ocurrencias
        Mono<Map<Long, Long>> capacitiesCountMapMono = capacityGateway.getAllCapacities(messageId)
                .filter(cap -> cap.idBootcamp() != null)
                .collect(Collectors.groupingBy(
                        BootcampCapacitiesResponse::idBootcamp,
                        Collectors.counting()
                )).doOnNext(list -> {
                    log.info("DEBUG - Lista de Bootcamps recuperada:");
                    log.info(" > " + list);
                    log.info("Total elementos en página: " + list.size());
                });

        //Combinamos ambos Monos y transformamos
        return Mono.zip(bootcampsMono, capacitiesCountMapMono, total)
                .map(tuple -> {
                    List<Bootcamp> bootcamps = tuple.getT1();
                    Map<Long, Long> countsMap = tuple.getT2();
                    Long totalBootcamps = tuple.getT3();

                    // Transformamos la lista de Bootcamps a BootcampReportDTO
                    List<BootcampCapacitiesReportDto> content = bootcamps.stream()
                            .map(b -> new BootcampCapacitiesReportDto(
                                    b.name(),
                                    countsMap.getOrDefault(b.id(), 0L)
                            ))
                            .sorted((o1, o2) -> {
                                // Comparador dinámico
                                if ("DESC".equalsIgnoreCase(sortBy)) {
                                    return Long.compare(o2.getCantCapacities(), o1.getCantCapacities()); // Mayor a menor
                                } else {
                                    return Long.compare(o1.getCantCapacities(), o2.getCantCapacities()); // Menor a mayor
                                }
                            })
                            .toList();
                    // Retornamos el objeto de paginación
                    return new PageResponse<>(
                            content,
                            totalBootcamps,
                            page,
                            size
                    );
                });

    }


    private Mono<CapacityBootcampSaveResult> saveCapacities(Long idBootcamp, Bootcamp bootcamp, String messageId) {
        return capacityGateway.saveCapacities(idBootcamp, bootcamp, messageId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_EMAIL)));
    }

    private Mono<Boolean> validateDuplicate(List<BootcampCapacty> bootcampTechnologies) {
        Set<BootcampCapacty> uniqueCapacities = new HashSet<>(bootcampTechnologies);
        if (bootcampTechnologies.size() != uniqueCapacities.size()) {
            return Mono.error(new BusinessException(TechnicalMessage.CAPACITY_DUPLICATE_IN_LIST));
        } else {
            return Mono.just(Boolean.FALSE);
        }
    }


}
