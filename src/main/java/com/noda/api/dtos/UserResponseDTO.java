package com.noda.api.dtos;

import java.time.LocalDate;

public record UserResponseDTO (
     Long id,
     String name,
     LocalDate birthday,
     String cpf,
     String email,
     AddressResponseDTO address
) {}
