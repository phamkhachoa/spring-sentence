package com.csv.mapper;

import com.csv.entity.TranslationEntity;
import com.csv.model.Translation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TranslationMapper {

    TranslationMapper MAPPER = Mappers.getMapper(TranslationMapper.class);
    TranslationEntity toEntity(Translation translation);
}
