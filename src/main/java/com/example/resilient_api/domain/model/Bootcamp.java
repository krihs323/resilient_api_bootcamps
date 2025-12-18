package com.example.resilient_api.domain.model;

import java.util.List;

public record Bootcamp(Long id, String name, String description, List<BootcampCapacty> bootcampCapacityList) {
}