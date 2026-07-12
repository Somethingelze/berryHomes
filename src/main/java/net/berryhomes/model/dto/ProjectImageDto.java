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
public class ProjectImageDto {

    private UUID id;
    private UUID projectId;
    private String filePath;
    private Integer sortOrder;
}