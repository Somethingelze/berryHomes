package net.berryhomes.mapper;

import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ProjectDocumentDto;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.dto.ProjectImageDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.model.entity.ProjectImage;
import org.mapstruct.*;

import java.util.ArrayList;

@Loggable
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE
)
public interface ProjectMapper {

    ProjectDto toProjectDto(Project project);

    @Mapping(target = "projectImages", ignore = true)
    @Mapping(target = "projectDocument", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Project toProjectEntity(ProjectDto projectDto);

    @Mapping(target = "projectId", ignore = true)
    ProjectDocumentDto toProjectDocumentDto(ProjectDocument projectDocument);
    @Mapping(target = "project", ignore = true)
    ProjectDocument toProjectDocumentEntity(ProjectDocumentDto projectDocumentDto);

    @Mapping(target = "projectId", ignore = true)
    ProjectImageDto toProjectImageDto(ProjectImage projectImage);
    @Mapping(target = "project", ignore = true)
    ProjectImage toProjectImageEntity(ProjectImageDto projectImageDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projectImages", ignore = true)
    @Mapping(target = "projectDocument", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateProjectFromDto(ProjectDto dto, @MappingTarget Project entity);

    @AfterMapping
    default void linkRelations(@MappingTarget Project project) {
        if (project.getProjectImages() != null) {
            project.getProjectImages().forEach(image -> image.setProject(project));
        } else {
            project.setProjectImages(new ArrayList<>());
        }
        if (project.getProjectDocument() != null) {
            project.getProjectDocument().setProject(project);
        }
    }
}
