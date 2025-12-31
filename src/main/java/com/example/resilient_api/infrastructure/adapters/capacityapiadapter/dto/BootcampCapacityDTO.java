package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class BootcampCapacityDTO {
    Long idBootcamp;
    Long idCapacity;
}
