package com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.resilient_api.domain.model.BootcampCapacty;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.BootcampCapacitiesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BootcampCapacitiesEntityMapper {

    @Mapping(source = "id_bootcamp", target = "id_bootcamp")// dominio es target, entidad es fuente
    @Mapping(source = "idCapacity", target = "idCapacity")// dominio es target, entidad es fuente
    BootcampCapacty toModel(BootcampCapacitiesEntity entity);
    //BootcampEntity toEntity(Bootcamp bootcamp);
}
