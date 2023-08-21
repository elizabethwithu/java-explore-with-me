package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    void removeCategoryById(Long id);

    List<CategoryDto> findAllCategories(Integer from, Integer size);

    CategoryDto findCategoryById(Long categoryId);
}
