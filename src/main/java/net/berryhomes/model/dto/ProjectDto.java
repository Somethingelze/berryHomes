package net.berryhomes.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private UUID id;

    @NotBlank(message = "Title in Russian is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String titleRu;

    @NotBlank(message = "Title in English is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String titleEn;

    @NotBlank(message = "Short in russian is required")
    private String shortDescRu;

    @NotBlank(message = "Short description in English is required")
    private String shortDescEn;

    private String descRu;

    private String descEn;

    @NotBlank
    private String location;

    private String reportFilePath;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private ZonedDateTime deletedAt;

    private List<ProjectImageDto> images;

    private List<ProjectDocumentDto> projectDocuments;
}
