package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dao.CompilationDao;
import ru.practicum.ewm.compilation.dto.CompilationInputDto;
import ru.practicum.ewm.compilation.dto.CompilationOutputDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationDao compilationDao;
    private final EventDao eventDao;

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
        return CompilationMapper.toCompilationDtoList(compilations);
    }

    @Override
    public CompilationOutputDto findCompilationById(Long compId) {
        Compilation compilation = compilationDao.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation", compId));
        log.info("Найдена подборка {}.", compilation.getId());

        return CompilationMapper.toCompilationOutputDto(compilation);
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

        return CompilationMapper.toCompilationOutputDto(savedCompilation);
    }

    @Override
    @Transactional
    public CompilationOutputDto updateCompilation(UpdateCompilationDto compilationDto, Long compId) {
        Compilation compilation = compilationDao.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation", compId));

        List<Event> events;

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getEvents() != null) {
            events = eventDao.findByIdIn(compilationDto.getEvents());
            compilation.setEvents(events);
        }

        Compilation savedCompilation = compilationDao.save(compilation);
        log.info("Категория {} успешно обновлена.", savedCompilation.getTitle());

        return CompilationMapper.toCompilationOutputDto(savedCompilation);
    }

    @Override
    @Transactional
    public void removeCompilationById(Long compId) {
        compilationDao.findById(compId).orElseThrow(() -> new NotFoundException("Compilation", compId));
        compilationDao.deleteById(compId);
        log.info("Подборка {} успешно удалена.", compId);
    }
}
