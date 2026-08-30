package net.berryhomes.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "project_images", schema = "berryhomes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "file_path", nullable = false, length = 550)
    private String filePath;

    @Builder.Default
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Builder.Default
    @Column(name = "focus_x", nullable = false)
    private Integer focusX = 50;

    @Builder.Default
    @Column(name = "focus_y", nullable = false)
    private Integer focusY = 50;
}
