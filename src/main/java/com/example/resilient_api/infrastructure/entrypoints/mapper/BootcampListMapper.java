package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.BootcampList;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampCapacitiesReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BootcampListMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "cantTechnologies", target = "cantTechnologies")
    BootcampCapacitiesReportDto bootcampListToBootcampListDTO(BootcampList bootcampList);

    BootcampList bootcampListDTOToBootcampList(BootcampCapacitiesReportDto bootcampCapacitiesReportDto);
}
