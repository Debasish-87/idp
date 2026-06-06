package com.idp.catalog.dto;

public record CreateServiceRequest(

        String name,

        String owner,

        String description

) {
}