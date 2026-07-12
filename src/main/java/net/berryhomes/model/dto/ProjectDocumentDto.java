package net.berryhomes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDocumentDto {

    private UUID id;
    private UUID projectId;
    private String titleRu;
    private String titleEn;
    private String category;
    private String filePath;
}