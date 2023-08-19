package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.CategoryDTO;
import com.newbies.birdy.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDTO toDTO(Category category);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productList", ignore = true)
    Category toEntity(CategoryDTO dto);
}
