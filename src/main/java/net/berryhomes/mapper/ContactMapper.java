package net.berryhomes.mapper;

import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.entity.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContactMapper {

    ContactDto toDto(Contact contact);

    Contact toEntity(ContactDto contactDto);
}
