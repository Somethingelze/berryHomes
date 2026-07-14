package net.berryhomes.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ContactDto(

        UUID id,

        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        @NotBlank
        @Size(min = 3, max = 100)
        @Email
        String email,

        @NotBlank
        @Size(min = 3, max = 30)
        String phone,

        @NotBlank
        @Size(max = 20)
        ContactType contactType,

        @NotBlank
        @Size(max = 20)
        ContactStatus contactStatus,

        @NotBlank
        @Size(min = 5)
        String message,
        ZonedDateTime createdAt
) {
}
