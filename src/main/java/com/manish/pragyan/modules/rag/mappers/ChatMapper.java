package com.manish.pragyan.modules.rag.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.manish.pragyan.modules.rag.dtos.ChatRequestDto;
import com.manish.pragyan.modules.rag.dtos.ChatResponseDto;
import com.manish.pragyan.modules.rag.entities.ChatEntity;
import com.manish.pragyan.modules.rag.entities.ChatType;

@Mapper(componentModel = "spring")
public interface ChatMapper {
	
	@BeanMapping(ignoreByDefault = true)
	@Mapping(source = "query", target = "content")
	ChatEntity dtoToEntity(ChatRequestDto chatRequestDto);
	
	ChatResponseDto entityToDto(ChatEntity chatEntity);

}
