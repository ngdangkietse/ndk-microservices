package dev.ngdangkiet.mapper;

import dev.ngdangkiet.DTO.EmailDTO;
import dev.ngdangkiet.domain.JsonMessage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author trongld
 * @since 11/16/2023
 */

@Mapper(config = ProtobufMapperConfig.class)
public interface EmailMapper extends ProtobufMapper<EmailDTO, JsonMessage> {

    EmailMapper INSTANCE = Mappers.getMapper(EmailMapper.class);
}