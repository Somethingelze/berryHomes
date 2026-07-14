package net.berryhomes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
public record ProjectDocumentDto (

        UUID id,
        UUID projectId,
        String titleRu,
        String titleEn,
        String category,
        String filePath
){
}