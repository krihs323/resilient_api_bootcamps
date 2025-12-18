package com.example.resilient_api.domain.model;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int page,
        int size
) {}