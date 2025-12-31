package com.example.resilient_api.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class CapacityDTO {

    @Digits(integer = 3, fraction = 0, message = "Id de la capacidad invalida")
    @PositiveOrZero
    private Long idCapacity;

    private String name;
    private String description;
    private List<TechnologyDTO> capacityTechnologyList;

}
