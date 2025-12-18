package com.example.resilient_api.infrastructure.adapters.emailvalidatoradapter.mapper;

import com.example.resilient_api.domain.model.Bootcamp;
import com.example.resilient_api.domain.model.BootcampCapacty;
import com.example.resilient_api.infrastructure.adapters.emailvalidatoradapter.dto.BootcampCapacitiesDTO;
import com.example.resilient_api.infrastructure.adapters.emailvalidatoradapter.dto.BootcampCapacityDTO;
import com.example.resilient_api.infrastructure.entrypoints.mapper.BootcampCapacitiesMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BootcampCapacityGatewayMapper {

    @Mapping(source = "id_bootcamp", target = "idBootcamp")
    @Mapping(source = "idCapacity", target = "idCapacity")
    BootcampCapacityDTO toDTO(BootcampCapacty bootcampCapacty);
}
/*
public record BootcampCapacityDTO(Long idBootcamp, Long idCapacity) {
}

public record BootcampCapacty(Long id, Long id_bootcamp, Long idCapacity) {
}


*/
