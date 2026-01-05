package com.example.resilient_api.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Technology(@JsonProperty("idTechnology") Long id, String name, String description) {
}