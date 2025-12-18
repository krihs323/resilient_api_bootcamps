package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.BootcampCapacty;
import com.example.resilient_api.domain.model.BootcampList;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampCapacitiesEntity;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampCapacitiesEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampCapacitiesRepository;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampListMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class BootcampPersistenceAdapter implements BootcampPersistencePort {
    private final BootcampRepository bootcampRepository;
    private final BootcampEntityMapper bootcampEntityMapper;

    private final BootcampCapacitiesRepository bootcampCapacitiesRepository;
    private final BootcampCapacitiesEntityMapper bootcampCapacitiesEntityMapper;

    private final BootcampListMapper bootcampListMapper;

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return bootcampRepository
                .save(bootcampEntityMapper.toEntity(bootcamp))
                .map(bootcampEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return bootcampRepository.findByName(name)
                .map(bootcampEntityMapper::toModel)
                .map(bootcamp -> true)  // Si encuentra el bootcamp, devuelve true
                .defaultIfEmpty(false);  // Si no encuentra, devuelve false
    }


    @Override
    public Flux<BootcampCapacitiesReportDto> listCapacitiesPage(int page, int size, String sortBy, String sortDir, String messageId) {

        String sql = """
            select capacities.description as name, count(capacities.id) as cantTechnologies from capacities_x_tecnologies inner join capacities on\s
                            capacities_x_tecnologies.id_bootcamp  = capacities.id
                            GROUP by capacities.id
                            ORDER BY %s %s
            LIMIT :limit OFFSET :offset
            """.formatted(sortBy, sortDir);;
        return databaseClient.sql(sql)
                .bind("limit", size )
                .bind("offset", page)
                .map((row, meta) -> new BootcampCapacitiesReportDto(
                        row.get("name", String.class),
                        row.get("cantTechnologies", Long.class)
                ))
                .all();
    }

    @Override
    public Mono<Long> countGroupedCapacities() {
        return bootcampCapacitiesRepository.countGroupedCapacities();
    }

    @Override
    public Flux<BootcampList> findCapabilitiesOrderedByName(
            int offset,
            int limit,
            String sortBy, String sortDir, String messageId
    ) {
        String sql = """
            select capacities.description as name, count(capacities.id) as cantTechnologies from capacities_x_tecnologies inner join capacities on\s
                            capacities_x_tecnologies.id_bootcamp  = capacities.id
                            GROUP by capacities.id
            LIMIT :limit OFFSET :offset
            """;
        return databaseClient.sql(sql)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, meta) -> new BootcampList(
                        row.get("name", String.class),
                        row.get("cantTechnologies", Long.class)
                ))
                .all();
    }

}
