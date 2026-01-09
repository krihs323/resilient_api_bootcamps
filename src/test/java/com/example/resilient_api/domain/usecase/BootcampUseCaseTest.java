package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.domain.spi.CapacityGateway;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampCapacitiesResponse;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private BootcampPersistencePort bootcampPersistencePort;

    @Mock
    private CapacityGateway capacityGateway;

    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    private Bootcamp sampleBootcamp;
    private final String messageId = "trace-456";

    List<BootcampCapacty> capacities;
    LocalDate bootcampDate;

    @BeforeEach
    void setUp() {
        bootcampDate = LocalDate.now().plusDays(15);
        capacities = List.of(
                new BootcampCapacty(1L, 10L, 1L),
                new BootcampCapacty(1L, 10L, 2L)
        );
        sampleBootcamp = new Bootcamp(
                1L,
                "Java Microservices Bootcamp",
                "A comprehensive course on WebFlux and Spring Cloud",
                bootcampDate,
                12, // Duración en semanas
                capacities
        );
    }

    @Test
    @DisplayName("Should register bootcamp and trigger async report")
    void registerBootcampSuccess() {
        // GIVEN
        when(bootcampPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));
        when(bootcampPersistencePort.save(any())).thenReturn(Mono.just(sampleBootcamp));
        when(capacityGateway.saveCapacities(anyLong(), any(), anyString()))
                .thenReturn(Mono.just(new CapacityBootcampSaveResult("SAVE", "PARTIAL")));

        // Mocks para el proceso background (Fire and Forget)
        when(capacityGateway.getCapacitiesByBootcamp(anyLong(), anyString())).thenReturn(Flux.empty());

        // WHEN
        Mono<Bootcamp> result = bootcampUseCase.registerBootcamp(sampleBootcamp, messageId);

        // THEN
        StepVerifier.create(result)
                .expectNext(sampleBootcamp)
                .verifyComplete();

        verify(bootcampPersistencePort).save(sampleBootcamp);
        verify(capacityGateway).saveCapacities(eq(1L), any(), eq(messageId));
        // Verificamos que se llamó al gateway para el reporte (aunque sea asíncrono)
        verify(capacityGateway, atLeastOnce()).getCapacitiesByBootcamp(eq(1L), eq(messageId));
    }

    @Test
    @DisplayName("Should throw error if bootcamp name is duplicated")
    void registerBootcampDuplicateName() {
        when(bootcampPersistencePort.existByName(anyString())).thenReturn(Mono.just(true));

        Mono<Bootcamp> result = bootcampUseCase.registerBootcamp(sampleBootcamp, messageId);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        ((BusinessException) e).getTechnicalMessage() == TechnicalMessage.BOOTCAMP_ALREADY_EXISTS)
                .verify();
    }

    @Test
    @DisplayName("Should generate paged report with dynamic sorting")
    void listBootcampsPageSorting() {
        // GIVEN: Dos bootcamps para forzar el comparador dinámico
        Bootcamp b1 = new Bootcamp(1L, "Alpha", "desc", bootcampDate, 2, capacities);
        Bootcamp b2 = new Bootcamp(2L, "Beta", "desc", bootcampDate, 1, capacities);

        when(bootcampPersistencePort.countBootcamps()).thenReturn(Mono.just(2L));
        when(bootcampPersistencePort.listBootcampsPage(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(b1, b2));

        // Mock de respuestas del gateway para contar (b1 tiene 1 cap, b2 tiene 2 caps)
        BootcampCapacitiesResponse r1 = new BootcampCapacitiesResponse(1L, 10L, 1L);
        BootcampCapacitiesResponse r2 = new BootcampCapacitiesResponse(2L, 10L, 2L);
        BootcampCapacitiesResponse r3 = new BootcampCapacitiesResponse(3L, 10L, 3L);

        when(capacityGateway.getAllCapacities(anyString())).thenReturn(Flux.just(r1, r2, r3));

        // WHEN: Pedimos orden DESC por cantidad de capacidades
        Mono<PageResponse<BootcampCapacitiesReportDto>> result =
                bootcampUseCase.listBootcampsPage(0, 10, "cantCapacities", "DESC", messageId);

        // THEN
        StepVerifier.create(result)
                .assertNext(response -> {
                    // Beta (2 caps) debe ir antes que Alpha (1 cap) por el DESC
                    assert response.content().get(0).getName().equals("Alpha");
                    assert response.content().get(0).getCantCapacities() == 0L;
                    assert response.content().get(1).getName().equals("Beta");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete bootcamp only if capacity deletion succeeds")
    void deleteBootcampSuccess() {
        when(capacityGateway.deleteCapacityByBootcamp(anyLong(), anyString())).thenReturn(Mono.just(true));
        when(bootcampPersistencePort.delete(anyLong(), anyString())).thenReturn(Mono.empty());

        Mono<Void> result = bootcampUseCase.deleteBootcamp(1L, messageId);

        StepVerifier.create(result).verifyComplete();

        verify(capacityGateway).deleteCapacityByBootcamp(1L, messageId);
        verify(bootcampPersistencePort).delete(1L, messageId);
    }

    @Test
    @DisplayName("Should get a bootcamp by idBootcamp")
    void getBootcamp() {
        // GIVEN
        when(bootcampPersistencePort.getBootcampById(anyLong(), anyString())).thenReturn(Mono.just(sampleBootcamp));

        // WHEN
        Mono<Bootcamp> result = bootcampUseCase.getBootcamp(anyLong(), anyString());

        // THEN
        StepVerifier.create(result)
                .expectNext(sampleBootcamp)
                .verifyComplete();

        verify(bootcampPersistencePort).getBootcampById(anyLong(), anyString());

    }

    @Test
    @DisplayName("Should return a flux of capacity technologies from gateway")
    void listCapacitiesByBootcampSuccess() {
        // 1. PREPARACIÓN (GIVEN)
        Long idBootcamp = 1L;

        // Creamos objetos de prueba (CapacityTechnologies)
        CapacityTechnologies cap1 = new CapacityTechnologies(
                10L, "Java Basics", "Desc",
                List.of(new Technology(1L, "Java", "Language"))
        );
        CapacityTechnologies cap2 = new CapacityTechnologies(
                11L, "Reactive Programming", "Desc",
                List.of(new Technology(2L, "WebFlux", "Framework"))
        );

        // Configuramos el Mock del Gateway para devolver un Flux con dos elementos
        when(capacityGateway.getCapacitiesByBootcamp(idBootcamp, messageId))
                .thenReturn(Flux.just(cap1, cap2));

        // 2. EJECUCIÓN (WHEN)
        Flux<CapacityTechnologies> result = bootcampUseCase.listCapacitiesByBootcamp(idBootcamp, messageId);

        // 3. VERIFICACIÓN (THEN)
        StepVerifier.create(result)
                .expectNext(cap1)
                .expectNext(cap2)
                .verifyComplete();

        // Verificamos que se llamó al gateway exactamente una vez con los parámetros correctos
        verify(capacityGateway, times(1)).getCapacitiesByBootcamp(idBootcamp, messageId);
    }
}