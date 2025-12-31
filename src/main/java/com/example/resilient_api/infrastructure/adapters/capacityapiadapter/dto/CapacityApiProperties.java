package com.example.resilient_api.infrastructure.adapters.capacityapiadapter.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("capacity-api")
public class CapacityApiProperties {
    private String baseUrl;
    private String apiKey;
    private String timeout;
}
