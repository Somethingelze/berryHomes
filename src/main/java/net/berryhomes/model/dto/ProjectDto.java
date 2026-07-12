package net.berryhomes.model.dto;

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
    private String titleRu;
    private String titleEn;
    private String shortDescRu;
    private String shortDescEn;
    private String descRu;
    private String descEn;
    private String location;
    private String reportFilePath;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime deletedAt;
    private List<ProjectImageDto> images;
    private List<ProjectDocumentDto> projectDocuments;
}
