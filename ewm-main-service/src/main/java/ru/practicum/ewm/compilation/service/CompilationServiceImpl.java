package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dao.CompilationDao;
import ru.practicum.ewm.compilation.dto.CompilationInputDto;
import ru.practicum.ewm.compilation.dto.CompilationOutputDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventServiceImpl;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationDao compilationDao;
    private final EventDao eventDao;
    private final EventServiceImpl eventService;

    @Override
    public List<CompilationOutputDto> findAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned) {
            compilations = compilationDao.findByPinned(true, pageRequest);
        } else {
            compilations = compilationDao.findAll(pageRequest).getContent();
        }
        log.info("Найден список из {} подборок.", compilations.size());

        List<CompilationOutputDto> compilationOutputDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationOutputDtoList.add(mapCompilationToDto(compilation));
        }
        return compilationOutputDtoList;
    }

    @Override
    public CompilationOutputDto findCompilationById(Long compId) {
        Compilation compilation = compilationDao.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation", compId));
        log.info("Найдена подборка {}.", compilation.getId());

        return mapCompilationToDto(compilation);
    }

    @Override
    @Transactional
    public CompilationOutputDto createCompilation(CompilationInputDto dto) {
        Compilation compilation = CompilationMapper.toCompilation(dto);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (!dto.getEvents().isEmpty()) {
            compilation.setEvents(eventDao.findByIdIn(dto.getEvents()));
        }

        Compilation savedCompilation = compilationDao.save(compilation);
        log.info("Создана новая категория {}.", savedCompilation.getId());

        return mapCompilationToDto(savedCompilation);
    }

    @Override
    @Transactional
    public CompilationOutputDto updateCompilation(CompilationInputDto compilationDto, Long compId) {
        Compilation compilation = compilationDao.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation", compId));

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventDao.findByIdIn(compilationDto.getEvents()));
        }

        Compilation savedCompilation = compilationDao.save(compilation);
        log.info("Категория {} успешно обновлена.", savedCompilation.getTitle());

        return mapCompilationToDto(savedCompilation);
    }

    @Override
    @Transactional
    public void removeCompilationById(Long compId) {
        checkCompilationAvailability(compilationDao, compId);
        compilationDao.deleteById(compId);
        log.info("Подборка {} успешно удалена.", compId);
    }

    private static void checkCompilationAvailability(CompilationDao dao, Long id) {
        if (!dao.existsById(id)) {
            throw new NotFoundException("Compilation", id);
        }
    }

    private CompilationOutputDto mapCompilationToDto(Compilation compilation) {
        CompilationOutputDto compilationDto = CompilationMapper.toCompilationOutputDto(compilation);

        List<Long> ids = compilationDto.getEvents().stream().map(EventShortDto::getId).collect(Collectors.toList());
        Map<Long, Long> views = eventService.getViews(ids);
        List<EventShortDto> eventShortDtos = compilationDto.getEvents();
        for (EventShortDto event : eventShortDtos) {
            event.setViews(views.getOrDefault(event.getId(), 0L));
        }
        compilationDto.setEvents(eventShortDtos);
        return compilationDto;
    }
}
