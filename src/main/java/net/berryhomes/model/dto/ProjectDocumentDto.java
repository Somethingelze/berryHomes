package net.berryhomes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.UUID;

@Builder
public record ProjectDocumentDto (

        UUID id,
        UUID projectId,
        String filePath
) implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}