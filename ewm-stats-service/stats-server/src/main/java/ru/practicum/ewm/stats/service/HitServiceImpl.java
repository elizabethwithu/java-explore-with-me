package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitInputDto;
import ru.practicum.ewm.HitOutputDto;
import ru.practicum.ewm.stats.dao.HitDao;
import ru.practicum.ewm.stats.exception.NotValidParamException;
import ru.practicum.ewm.stats.mapper.HitMapper;
import ru.practicum.ewm.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitDao hitDao;

    @Transactional
    public void addHit(HitInputDto hitInputDto) {
        Hit hit = HitMapper.toHit(hitInputDto);
        Hit savedHit = hitDao.save(hit);
        log.info("Успешно добавлен {} с айди {}.", savedHit.getApp(), savedHit.getId());
    }

    public List<HitOutputDto> getHitStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new NotValidParamException("Дата начала раньше даты окончания.");
        }

        List<HitOutputDto> hitStats;
        if (uris != null) {
            if (unique) {
                hitStats = hitDao.findAllByTimestampAndUrisAndUniqueIp(start, end, uris);
                log.info("Получена статистика по uri и уникальному ip.");
            } else {
                hitStats = hitDao.findAllByTimestampAndUris(start, end, uris);
                log.info("Получена статистика по uri.");
            }
        } else {
            if (unique) {
                hitStats = hitDao.findAllByTimestampAndUniqueIp(start, end);
                log.info("Получена статистика по уникальному ip.");
            } else {
                hitStats = hitDao.findAllByTimestamp(start, end);
                log.info("Получена статистика.");
            }
        }
        return hitStats;
    }
}
