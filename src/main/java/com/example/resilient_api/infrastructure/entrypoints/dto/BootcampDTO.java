package com.example.resilient_api.infrastructure.entrypoints.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class BootcampDTO {
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres.")
    private String name;

    @NotBlank(message = "La descripcion no puede estar vacía")
    @Size(min = 1, max = 50, message = "La descripcion debe tener entre 1 y 90 caracteres.")
    private String description;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd") // Asegura el formato compatible con DATE de MySQL
    private LocalDate launchDate;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración mínima debe ser de 1 semana")
    @Max(value = 52, message = "La duración no puede exceder las 52 semanas")
    private Integer durationWeeks;

    @Size.List({
            @Size(min = 1, message = "Minimo 1 capacidad"),
            @Size(max = 4, message = "maximo 4 capacidad")
    })
    private List<CapacityDTO> bootcampCapacityList;


}
