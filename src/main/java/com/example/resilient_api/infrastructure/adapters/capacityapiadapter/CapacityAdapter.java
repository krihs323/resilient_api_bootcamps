package com.example.resilient_api.infrastructure.adapters.capacityapiadapter;

import com.example.resilient_api.domain.constants.Messages;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.exceptions.TechnicalException;
import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.CapacityBootcampSaveResult;
import com.example.resilient_api.domain.model.CapacityTechnologies;
import com.example.resilient_api.domain.spi.CapacityGateway;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampCapacitiesDTO;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampCapacitiesResponse;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.CapacityApiResponse;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.CapacityApiProperties;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.mapper.BootcampCapacitiesGatewayMapper;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.util.Constants;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacityAdapter implements CapacityGateway {

    private final WebClient webClient;
    private final CapacityApiProperties capacityApiProperties;
    private final Retry retry;
    private final Bulkhead bulkhead;

    private final BootcampCapacitiesGatewayMapper bootcampCapacitiesGatewayMapper;

    @Value("${capicity-api}")
    private String capacityPath;

    @Override
    @CircuitBreaker(name = "capacityApiValidator", fallbackMethod = "fallback")
    public Mono<CapacityBootcampSaveResult> validateName(String name, String messageId) {
        log.info("Starting email validation for email: {} with messageId: {}", name, messageId);
        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("api_key", capacityApiProperties.getApiKey())
                            .queryParam("email", name)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.ADAPTER_RESPONSE_NOT_FOUND))
                    .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.INTERNAL_ERROR_IN_ADAPTERS))
                    .bodyToMono(CapacityApiResponse.class)
                    .doOnNext(response -> log.info("Received API response for messageId: {}: {}", messageId, response))
                    .filter(response -> "DELIVERABLE".equalsIgnoreCase(response.deliverability()))
                    .map(response -> new CapacityBootcampSaveResult(
                            response.deliverability(),
                            response.quality_score()
                    ))
                    .transformDeferred(RetryOperator.of(retry))
                    .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
                    .doOnTerminate(() -> log.info("Completed email validation process for messageId: {}", messageId))
                    .doOnError(e -> log.error("Error occurred in email validation for messageId: {}", messageId, e));
    }

    @Override
    @CircuitBreaker(name = "capacityApiValidator", fallbackMethod = "fallback")
    public Mono<CapacityBootcampSaveResult> saveCapacities(Long idBootcamp, Bootcamp bootcamp, String messageId) {
        log.info("Starting save bootcamp for bootcamp: {} with messageId: {}", bootcamp, messageId);
        BootcampCapacitiesDTO bootcampCapacitiesDTO = bootcampCapacitiesGatewayMapper.toDTO(bootcamp);
        bootcampCapacitiesDTO.setIdBootcamp(idBootcamp);
        return webClient.post()
        .uri(capacityPath + "capacity/bootcamp")
                // Definir el tipo de contenido (JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(Messages.MSJ_HEADER.getValue(), messageId)
                // Pasar el objeto en el cuerpo (se serializa automáticamente a JSON)
                .bodyValue(bootcampCapacitiesDTO)
                .retrieve()
                // Manejo de errores basado en códigos de estado HTTP
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        buildErrorResponse(response, TechnicalMessage.INTERNAL_ERROR_IN_ADAPTERS))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException(Messages.MSJ_SERVER_ERROR.getValue())))
                // Mapear el cuerpo de la respuesta a un objeto
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Received save API response for messageId {}: {}", messageId, response))
                .map(response -> {
                    String status = response.contains("successfully") ? "SAVED" : "PARTIAL";
                    return new CapacityBootcampSaveResult(status, "1");
                })
                .doOnTerminate(() -> log.info("Completed capacity saving process for messageId: {}", messageId))
                .doOnError(e -> log.error("Error saving capacities for messageId: {}", messageId, e));
    }


    @Override
    public Flux<BootcampCapacitiesResponse> getAllCapacities( String messageId) {
        log.info("Starting get all bootcamp x capacities");
        return webClient.get()
                .uri(capacityPath + "capacity/bootcamp")
                // Definir el tipo de contenido (JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(Messages.MSJ_HEADER.getValue(), messageId)
                .retrieve()
                // Manejo de errores basado en códigos de estado HTTP
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        buildErrorResponse(response, TechnicalMessage.INTERNAL_ERROR_IN_ADAPTERS))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException(Messages.MSJ_SERVER_ERROR.getValue())))
                // Mapear el cuerpo de la respuesta a un objeto
                .bodyToFlux(BootcampCapacitiesResponse.class)
                .doOnNext(response -> log.info("Received save API response for messageId {}: {}", messageId, response))
                .doOnTerminate(() -> log.info("Completed bootcamps x capacities get process for messageId: {}", messageId))
                .doOnError(e -> log.error("Error gettin all capacities for messageId: {}", messageId, e));
    }

    @Override
    public Flux<CapacityTechnologies> getCapacitiesByBootcamp(Long idBootcamp, String messageId) {
        log.info("Starting get all capacities by bootcamp");
        return webClient.get()
                .uri(capacityPath + "capacity/capacities-by-Bootcamps/?idBootcamp="+idBootcamp)
                // Definir el tipo de contenido (JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(Messages.MSJ_HEADER.getValue(), messageId)
                .retrieve()
                // Manejo de errores basado en códigos de estado HTTP
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        buildErrorResponse(response, TechnicalMessage.INTERNAL_ERROR_IN_ADAPTERS))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException(Messages.MSJ_SERVER_ERROR.getValue())))
                // Mapear el cuerpo de la respuesta a un objeto
                .bodyToFlux(CapacityTechnologies.class)
                .doOnNext(response -> log.info("Received API response for messageId {}: {}", messageId, response))
                .doOnTerminate(() -> log.info("Completed bootcamps x capacities get process for messageId: {}", messageId))
                .doOnError(e -> log.error("Error saving capacities for messageId: {}", messageId, e));
    }

    @Override
    public Mono<Boolean> deleteCapacityByBootcamp(Long id, String messageId) {
        log.info("Starting Bootcamp deletion for: {} with messageId: {}", id, messageId);

        return webClient.delete() // Usamos el verbo DELETE
                .uri(uriBuilder -> uriBuilder
                        .path("capacity/{id}") // Asumiendo que el nombre va en el path o usa queryParam según tu API
                        .queryParam("api_key", capacityApiProperties.getApiKey())
                        .build(id))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(Messages.MSJ_HEADER.getValue(), messageId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        buildErrorResponse(response, TechnicalMessage.INTERNAL_ERROR_IN_ADAPTERS))
                .onStatus(status -> status.value() == 400, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new BusinessException(TechnicalMessage.CAPACITY_WITH_OTHER_BOOTCAMPS)))
                )
                .toBodilessEntity()
                .map(response -> {
                    // Esto devolverá true si el status es 200, 201, 204, etc.
                    boolean isSuccess = response.getStatusCode().is2xxSuccessful();
                    log.info("API CAPACITY response for messageId {}: Status {}", messageId, response.getStatusCode());
                    return isSuccess;
                })
                .timeout(Duration.ofSeconds(15)) // Aumentamos el tiempo de espera a 15 segundos
                .doOnError(e -> log.error("Timeout real detectado: {}", e.getMessage()));
    }

    public Mono<CapacityBootcampSaveResult> fallback(Throwable t) {
        return Mono.defer(() ->
                Mono.justOrEmpty(t instanceof TimeoutException
                                ? new CapacityBootcampSaveResult("UNKOWN", "0.0") // Respuesta por timeout
                                : null)
                        .switchIfEmpty(Mono.error(t))  // Si no es timeout, lanza el error
        );
    }

    private Mono<Throwable> buildErrorResponse(ClientResponse response, TechnicalMessage technicalMessage) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty(Constants.NO_ADITIONAL_ERROR_DETAILS)
                .flatMap(errorBody -> {
                    log.error(Constants.STRING_ERROR_BODY_DATA, errorBody);
                    return Mono.error(
                            response.statusCode().is5xxServerError() ?
                                    new TechnicalException(technicalMessage):
                                    new BusinessException(technicalMessage));
                });
    }
}
