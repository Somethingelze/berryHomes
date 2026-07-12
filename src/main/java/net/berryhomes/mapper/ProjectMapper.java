package net.berryhomes.mapper;

import net.berryhomes.model.dto.ProjectDocumentDto;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.dto.ProjectImageDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.model.entity.ProjectImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

    ProjectDto toProjectDto(Project project);
    Project toProjectEntity(ProjectDto projectDto);

    @Mapping(source = "project.id", target = "projectId")
    ProjectDocumentDto toProjectDocumentDto(ProjectDocument projectDocument);
    ProjectDocument toProjectDocumentEntity(ProjectDocumentDto projectDocumentDto);

    @Mapping(source = "project.id", target = "projectId")
    ProjectImageDto toProjectImageDto(ProjectImage projectImage);
    ProjectImage toProjectImageEntity(ProjectImageDto projectImageDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateProjectFromDto(ProjectDto dto, @MappingTarget Project entity);}
