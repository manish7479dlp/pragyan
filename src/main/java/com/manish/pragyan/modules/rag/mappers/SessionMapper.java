package com.manish.pragyan.modules.rag.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.manish.pragyan.modules.rag.dtos.SessionResponseDto;
import com.manish.pragyan.modules.rag.dtos.SessionUpdateDto;
import com.manish.pragyan.modules.rag.entities.SessionEntity;

@Mapper(componentModel = "spring")
public interface SessionMapper {

	@BeanMapping(ignoreByDefault = true)
	@Mapping(source = "id", target = "id")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "isPinned", target = "isPinned")
	SessionResponseDto entityToDto(SessionEntity sessionEntity);

	
	@BeanMapping(ignoreByDefault = true)
	@Mapping(source = "name", target = "name")
	@Mapping(source = "isPinned", target = "isPinned")
	void updateSessionDtoToEntity(SessionUpdateDto dto, @MappingTarget SessionEntity entity);

}
