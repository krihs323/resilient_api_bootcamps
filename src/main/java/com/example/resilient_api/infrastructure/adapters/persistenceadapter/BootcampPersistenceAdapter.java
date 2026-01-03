package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.spi.BootcampPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampCapacitiesEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.BootcampCapacitiesRepository;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampListMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

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
    public Flux<Bootcamp> listBootcampsPage(int page, int size, String sortBy, String sortDir, String messageId) {

        String orderBy = "ORDER BY";
        if(!sortBy.equalsIgnoreCase("name")){
            orderBy = "";
            sortBy = "";
            sortDir = "";
        }

        String sql = """
            select id, name, description, launch_date, duration_weeks
            FROM bootcamps
            %S %s %s
            LIMIT :limit OFFSET :offset
            """.formatted(orderBy, sortBy, sortDir);
        return databaseClient.sql(sql)
                .bind("limit", size )
                .bind("offset", page)
                .map((row, meta) -> {
                    BootcampEntity bootcamp = new BootcampEntity();
                    bootcamp.setId(row.get("id", Long.class));
                    bootcamp.setName(row.get("name", String.class));
                    bootcamp.setDescription(row.get("description", String.class));
                    bootcamp.setLaunchDate(row.get("launch_date", LocalDate.class));
                    bootcamp.setDurationWeeks(row.get("duration_weeks", Integer.class));
                    return bootcamp;
                })
                .all().map(bootcampEntityMapper::toModel);
    }

    @Override
    public Mono<Long> countBootcamps() {
        return bootcampRepository.count();
    }

    @Override
    public Mono<Void> delete(Long id, String messageId) {
        return bootcampRepository.deleteById(id);
    }

}
