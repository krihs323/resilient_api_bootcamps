package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.mapper;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampCapacitiesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {BootcampCapacityGatewayMapper.class})
public interface BootcampCapacitiesGatewayMapper {

    @Mapping(source = "id", target = "idBootcamp")
    @Mapping(source = "bootcampCapacityList", target = "bootcampCapacityList")
    BootcampCapacitiesDTO toDTO(Bootcamp bootcamp);
}
