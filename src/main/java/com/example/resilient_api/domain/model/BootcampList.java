package com.example.resilient_api.domain.model;

public class BootcampList {

    String name;
    Long cantCapacities;


    public BootcampList() {
    }

    public BootcampList(String name, Long cantCapacities) {
        this.name = name;
        this.cantCapacities = cantCapacities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getcantCapacities() {
        return cantCapacities;
    }

    public void setcantCapacities(Long cantCapacities) {
        this.cantCapacities = cantCapacities;
    }
}
