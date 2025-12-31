package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class BootcampCapacitiesDTO {
    Long idBootcamp;
    List<BootcampCapacityDTO> bootcampCapacityList;
}
