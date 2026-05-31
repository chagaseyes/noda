package com.noda.api.dtos;

public record AddressResponseDTO(
        String cep,
        String street,
        String number,
        String city,
        String state,
        String neighborhood,
        String complement
) {}
