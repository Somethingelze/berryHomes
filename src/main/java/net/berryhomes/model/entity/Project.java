package net.berryhomes.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title_ru", nullable = false)
    private String titleRu;

    @Column(name = "title_en", nullable = false)
    private String titleEn;

    @Column(name = "short_desc_ru", columnDefinition = "TEXT")
    private String shortDescRu;

    @Column(name = "short_desc_en", columnDefinition = "TEXT")
    private String shortDescEn;

    @Column(name = "desc_ru", columnDefinition = "TEXT")
    private String descRu;

    @Column(name = "desc_en", columnDefinition = "TEXT")
    private String descEn;

    @Column(name = "location")
    private String location;

    @Column(name = "report_file_path", length = 550)
    private String reportFilePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectImage> projectImages = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectDocument> projectDocuments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}
