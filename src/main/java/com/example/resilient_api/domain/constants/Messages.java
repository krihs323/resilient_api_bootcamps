package com.example.resilient_api.domain.constants;

public enum Messages {

    MSJ_SERVER_ERROR("Error en el servidor"),
    MSJ_HEADER("x-message-id");

    private String value;

    Messages(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
