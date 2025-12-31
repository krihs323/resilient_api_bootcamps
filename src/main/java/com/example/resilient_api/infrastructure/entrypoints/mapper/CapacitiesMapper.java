package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.CapacityTechnologies;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {TechnologyMapper.class})
public interface CapacitiesMapper {

    @Mapping( source = "id", target = "idCapacity")
    @Mapping( source = "name", target = "name")
    @Mapping( source = "description", target = "description")
    @Mapping( source = "capacityTechnologyList", target = "capacityTechnologyList")
    CapacityDTO capacityTechnologiesToCapacityDTO(CapacityTechnologies capacityTechnologies);
}
