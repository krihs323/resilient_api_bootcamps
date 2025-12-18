package com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capacities_x_tecnologies")
@Getter
@Setter
@RequiredArgsConstructor
public class BootcampCapacitiesEntity {
    @Id
    private Long id;
    private Long idCapacity;
    private Long id_bootcamp;
}
