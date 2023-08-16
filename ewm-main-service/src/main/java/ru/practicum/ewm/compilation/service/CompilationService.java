package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationInputDto;
import ru.practicum.ewm.compilation.dto.CompilationOutputDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationOutputDto> findAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationOutputDto findCompilationById(Long compId);

    CompilationOutputDto createCompilation(CompilationInputDto dto);

    CompilationOutputDto updateCompilation(UpdateCompilationDto compilationDto, Long compId);

    void removeCompilationById(Long compId);
}
