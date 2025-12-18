package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.constants.Constants;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.CapacityGateway;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.infrastructure.adapters.emailvalidatoradapter.dto.BootcampCapacitiesDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BootcampUseCase implements BootcampServicePort {

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacityGateway validatorGateway;

    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort, CapacityGateway validatorGateway) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.validatorGateway = validatorGateway;
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

//          .flatMap(savedBootcamp -> saveCapacities(savedBootcamp.id(), bootcamp, messageId)
//                .flatMap(validationResult -> {
//                    // 4. Validar el resultado del servicio externo
//                    if (Constants.DELIVERABLE.equalsIgnoreCase(validationResult.deliverability())) {
//                        return Mono.just(savedBootcamp);
//                    } else {
//                        // Si falla la validación externa, podrías necesitar compensar (borrar el bootcamp)
//                        // o simplemente lanzar el error.
//                        return Mono.error(new BusinessException(TechnicalMessage.INVALID_EMAIL));
//                    }
//                })
//        );


    }


    @Override
    public Mono<PageResponse<BootcampCapacitiesReportDto>> listCapacitiesPage(int page, int size, String sortBy, String sortDir, String messageId) {
        var data = bootcampPersistencePort.listCapacitiesPage(page, size, sortBy, sortDir, messageId).collectList();
        var total = bootcampPersistencePort.countGroupedCapacities();

        return Mono.zip(data, total)
                .map(tuple -> new PageResponse<>(
                        tuple.getT1(),
                        tuple.getT2(),
                        page,
                        size
                ));
    }

    @Override
    public Flux<BootcampList> listCapacities(int page, int size, String sortBy, String sortDir, String messageId) {
        return bootcampPersistencePort.findCapabilitiesOrderedByName(page, size, sortBy, sortDir, messageId);
    }

    private Mono<CapacityBootcampSaveResult> saveCapacities(Long idBootcamp, Bootcamp bootcamp, String messageId) {
        return validatorGateway.saveCapacities(idBootcamp, bootcamp, messageId)
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
