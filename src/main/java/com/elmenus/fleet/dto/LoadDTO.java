package com.elmenus.fleet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record LoadDTO(

    @NotEmpty(message = "Medication codes must not be empty")
    List<@Pattern(
        regexp = "^[A-Z0-9_]*$",
        message = "Medication code can only contain upper case letters, numbers, and underscores"
    ) String> medicationCodes

) {
}
