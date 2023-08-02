package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.HitInputDto;
import ru.practicum.ewm.stats.HitOutputDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void addHit(HitInputDto hitInputDto);

    List<HitOutputDto> getHitStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
