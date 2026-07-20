package net.berryhomes.mapper;

import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.entity.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Loggable
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContactMapper {

    ContactDto toDto(Contact contact);
    Contact toEntity(ContactDto contactDto);
}
