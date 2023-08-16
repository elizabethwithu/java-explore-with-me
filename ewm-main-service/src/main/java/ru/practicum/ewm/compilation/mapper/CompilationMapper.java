package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationInputDto;
import ru.practicum.ewm.compilation.dto.CompilationOutputDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(CompilationInputDto dto) {

        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .build();
    }

    public static CompilationOutputDto toCompilationOutputDto(Compilation compilation) {
        List<EventShortDto> events = new ArrayList<>();
        if (compilation.getEvents() != null) {
            events = EventMapper.toEventShortDtoList(compilation.getEvents());
        }
        return new CompilationOutputDto(
                compilation.getId(),
                compilation.getTitle(),
                events,
                compilation.getPinned()
        );
    }

    public static List<CompilationOutputDto> toCompilationDtoList(Iterable<Compilation> compilations) {
        List<CompilationOutputDto> result = new ArrayList<>();

        for (Compilation compilation : compilations) {
            result.add(toCompilationOutputDto(compilation));
        }
        return result;
    }
}
