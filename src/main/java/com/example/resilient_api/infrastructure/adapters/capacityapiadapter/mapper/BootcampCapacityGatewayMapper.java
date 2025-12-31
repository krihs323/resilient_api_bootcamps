package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.mapper;

import com.example.resilient_api.domain.model.BootcampCapacty;
import com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto.BootcampCapacityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BootcampCapacityGatewayMapper {

    @Mapping(source = "idBootcamp", target = "idBootcamp")
    @Mapping(source = "idCapacity", target = "idCapacity")
    BootcampCapacityDTO toDTO(BootcampCapacty bootcampCapacty);
}