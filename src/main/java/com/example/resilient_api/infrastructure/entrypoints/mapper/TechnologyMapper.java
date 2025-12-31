package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.infrastructure.entrypoints.dto.TechnologyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TechnologyMapper {

    @Mapping(source = "idTechnology", target = "id")
    Technology toTechnology(TechnologyDTO technologyDto);

    @Mapping(source = "id", target = "idTechnology")
    TechnologyDTO toTechnologyDTO(Technology technology);
}
