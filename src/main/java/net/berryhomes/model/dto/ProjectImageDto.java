package net.berryhomes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
public record ProjectImageDto (
        UUID id,
        UUID projectId,
        String filePath,
        Integer sortOrder
){
}