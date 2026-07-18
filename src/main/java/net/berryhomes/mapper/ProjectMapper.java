package net.berryhomes.mapper;

import lombok.Builder;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ProjectDocumentDto;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.dto.ProjectImageDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.model.entity.ProjectImage;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Loggable
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

    ProjectDto toProjectDto(Project project);
    Project toProjectEntity(ProjectDto projectDto);

    @Mapping(target = "projectId", ignore = true)
    ProjectDocumentDto toProjectDocumentDto(ProjectDocument projectDocument);
    ProjectDocument toProjectDocumentEntity(ProjectDocumentDto projectDocumentDto);

    @Mapping(target = "projectId", ignore = true)
    ProjectImageDto toProjectImageDto(ProjectImage projectImage);
    ProjectImage toProjectImageEntity(ProjectImageDto projectImageDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateProjectFromDto(ProjectDto dto, @MappingTarget Project entity);

    @AfterMapping
    default void linkRelations(@MappingTarget Project project) {
        if (project.getProjectImages() != null) {
            project.getProjectImages().forEach(image -> image.setProject(project));
        }
        if (project.getProjectDocuments() != null) {
            project.getProjectDocuments().forEach(document -> document.setProject(project));
        }
    }
}
