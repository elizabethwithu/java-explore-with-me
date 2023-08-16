package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dao.CategoryDao;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    private final EventDao eventDao;

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        Category category = CategoryMapper.toCategory(dto);
        Category savedCategory = categoryDao.save(category);
        log.info("Создана новая категория {}.", savedCategory.getId());

        return CategoryMapper.toCategoryDto(categoryDao.save(savedCategory));
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category oldCategory = categoryDao.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category", categoryId));

        oldCategory.setName(categoryDto.getName());

        Category updatedCategory = categoryDao.save(oldCategory);
        log.info("Категория {} успешно обновлена.", categoryId);

        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Transactional
    @Override
    public void removeCategoryById(Long id) {
        categoryDao.findById(id).orElseThrow(() -> new NotFoundException("Category", id));
        List<Event> events = eventDao.findFirstByCategoryId(id);

        if (!events.isEmpty()) {
            throw new ConflictException("Категория содержит события.");
        }

        categoryDao.deleteById(id);
        log.info("Категория {} успешно удалена.", id);
    }

    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        Page<Category> categories = categoryDao.findAll(pageRequest);
        log.info("Список категорий получен, их количество: {}.", categories.getTotalElements());

        return CategoryMapper.toCategoryDtoList(categories);
    }

    @Override
    public CategoryDto findCategoryById(Long categoryId) {
        Category category = categoryDao.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category", categoryId));
        log.info("Найдена категория {}.", categoryId);

        return CategoryMapper.toCategoryDto(category);
    }
}
