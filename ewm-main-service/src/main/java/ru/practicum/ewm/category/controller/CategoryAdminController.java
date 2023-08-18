package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto dto) {
        return categoryService.createCategory(dto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable("catId") Long categoryId,
                                      @Valid @RequestBody CategoryDto categoryDto) {

        return categoryService.updateCategory(categoryDto, categoryId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoryById(@PathVariable Long catId) {
        categoryService.removeCategoryById(catId);
    }
}
