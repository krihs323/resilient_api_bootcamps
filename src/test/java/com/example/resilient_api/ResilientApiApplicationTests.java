package com.example.resilient_api;

import com.example.resilient_api.domain.api.BootcampServicePort;
import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.BootcampCapacty;
import com.example.resilient_api.domain.model.PageResponse;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityDTO;
import com.example.resilient_api.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampListMapper;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampMapper;
import com.example.resilient_api.infrastructure.validation.ObjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import static com.example.resilient_api.infrastructure.adapters.emailvalidatoradapter.util.Constants.X_MESSAGE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResilientApiApplicationTests {

    @Mock
    private BootcampServicePort bootcampServicePort;
    @Mock
    private BootcampMapper bootcampMapper;
    @Mock
    private BootcampListMapper bootcampListMapper;
    @Mock
    private ObjectValidator objectValidator;

    @InjectMocks
    private BootcampHandlerImpl bootcampHandler;

    private BootcampDTO bootcampDTO;
    private final String MESSAGE_ID = "test-uuid";

    @BeforeEach
    void setUp() {
        List<CapacityDTO> technologies = List.of(
                new CapacityDTO(1L),
                new CapacityDTO(2L),
                new CapacityDTO(3L)
        );
        bootcampDTO = new BootcampDTO();
        bootcampDTO.setName("Backend Java");
        bootcampDTO.setId(1L);
        bootcampDTO.setName("Backend Specialist");
        bootcampDTO.setDescription("Capacidad enfocada en microservicios");
        bootcampDTO.setBootcampCapacityList(technologies);
    }

    @Test
    void createBootcampSuccess() {
        // GIVEN
        MockServerRequest request = MockServerRequest.builder()
                .header(X_MESSAGE_ID, MESSAGE_ID)
                .body(Mono.just(bootcampDTO));

        List<BootcampCapacty> bootcampTechnologies = List.of(
                new BootcampCapacty(1L, 1L, 100L),
                new BootcampCapacty(2L, 1L, 101L),
                new BootcampCapacty(3L, 1L, 102L)
        );

        //doNothing().when(objectValidator).validate(any());
        when(bootcampMapper.bootcampDTOToBootcamp(any())).thenReturn(new Bootcamp(1L, "java", "description", bootcampTechnologies));
        when(bootcampServicePort.registerBootcamp(any(), anyString()))
                .thenReturn(Mono.just(new Bootcamp(1L, "java", "description", bootcampTechnologies)));

        // WHEN
        Mono<ServerResponse> responseMono = bootcampHandler.createBootcamp(request);

        // THEN
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    void listBootcampSuccess() {
        // GIVEN
        MockServerRequest request = MockServerRequest.builder()
                .header(X_MESSAGE_ID, MESSAGE_ID)
                .queryParam("page", "0")
                .queryParam("size", "10")
                .build();

        PageResponse<BootcampCapacitiesReportDto> pageResponse = createMockPageResponse();
        when(bootcampServicePort.listCapacitiesPage(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(pageResponse));

        // WHEN
        Mono<ServerResponse> responseMono = bootcampHandler.listBootcamp(request);

        // THEN
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();
    }

    private PageResponse<BootcampCapacitiesReportDto> createMockPageResponse() {
        // Creamos una lista de datos de prueba
        List<BootcampCapacitiesReportDto> mockContent = List.of(
                new BootcampCapacitiesReportDto("Java Backend", 5L),
                new BootcampCapacitiesReportDto("Frontend React", 3L)
        );

        // Instanciamos el record con datos de paginación
        return new PageResponse<>(
                mockContent,
                2L,
                0,
                10
        );
    }

}
