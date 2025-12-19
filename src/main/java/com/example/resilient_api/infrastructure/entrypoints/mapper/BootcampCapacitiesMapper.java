package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.BootcampCapacty;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BootcampCapacitiesMapper {
    @Mapping(source = "idCapacity", target = "idCapacity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idBootcamp", ignore = true)
    BootcampCapacty toBootcampTechnology(CapacityDTO capacityDto);
}
