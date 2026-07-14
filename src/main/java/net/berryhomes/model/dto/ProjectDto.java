package net.berryhomes.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ProjectDto(

        UUID id,

        @NotBlank(message = "Title in Russian is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String titleRu,

        @NotBlank(message = "Title in English is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String titleEn,

        @NotBlank(message = "Short in russian is required")
        String shortDescRu,

        @NotBlank(message = "Short description in English is required")
        String shortDescEn,

        String descRu,

        String descEn,

        @NotBlank
        String location,

        String reportFilePath,

        ZonedDateTime createdAt,

        ZonedDateTime updatedAt,

        ZonedDateTime deletedAt,

        List<ProjectImageDto> images,

        List<ProjectDocumentDto> projectDocuments
) {
}
