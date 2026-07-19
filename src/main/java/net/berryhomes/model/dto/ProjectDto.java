package net.berryhomes.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ProjectDto(
        UUID id,

        @NotBlank
        @Size(max = 255)
        String address,
        @NotBlank
        @Size(max = 100)
        String cityZip,

        String purchasePrice,
        String monthlyRent,
        String renovationBudget,
        String estNoiAnnual,
        String totalInvestment,
        String cashOnCashReturn,
        String estPayback,

        List<ProjectImageDto> projectImages,
        ProjectDocumentDto projectDocument

) implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String getFirstImageUrl() {
        if (projectImages != null && !projectImages.isEmpty()) {
            return projectImages.getFirst().filePath();
        }
        return "";
    }
}