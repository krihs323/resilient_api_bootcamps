package com.example.resilient_api.domain.model;

public class BootcampList {

    String name;
    Long cantTechnologies;


    public BootcampList() {
    }

    public BootcampList(String name, Long cantTechnologies) {
        this.name = name;
        this.cantTechnologies = cantTechnologies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCantTechnologies() {
        return cantTechnologies;
    }

    public void setCantTechnologies(Long cantTechnologies) {
        this.cantTechnologies = cantTechnologies;
    }
}
