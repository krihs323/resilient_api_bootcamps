package com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.resilient_api.domain.model.BootcampList;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampCapacitiesEntity;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BootcampCapacitiesRepository extends ReactiveCrudRepository<BootcampCapacitiesEntity, Long> {

     @Query("SELECT c.name AS name, \n" +
            "               COUNT(ct.idCapacity) AS cantTechnologies \n" +
            "        FROM capacities c\n" +
            "        INNER JOIN capacities_x_tecnologies ct\n" +
            "            ON c.id = ct.id_bootcamp  \n" +
            "        GROUP BY c.id ")
    Flux<BootcampCapacitiesReportDto> findBootcampByTechnology(
            int size,
            long offset
    );

    @Query("""
            SELECT COUNT(DISTINCT c.id)
                    FROM capacities c
                    INNER JOIN capacities_x_tecnologies ct
                        ON c.id = ct.id_bootcamp        """)
    Mono<Long> countGroupedCapacities();

    @Query("SELECT c.name AS name, \n" +
            "               COUNT(ct.idCapacity) AS cantTechnologies \n" +
            "        FROM capacities c\n" +
            "        INNER JOIN capacities_x_tecnologies ct\n" +
            "            ON c.id = ct.id_bootcamp  \n" +
            "        GROUP BY c.id ")
    Flux<BootcampList> getAll();
}
