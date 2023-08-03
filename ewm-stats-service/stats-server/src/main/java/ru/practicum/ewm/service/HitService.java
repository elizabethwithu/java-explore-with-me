package ru.practicum.ewm.service;

import ru.practicum.ewm.HitInputDto;
import ru.practicum.ewm.HitOutputDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void addHit(HitInputDto hitInputDto);

    List<HitOutputDto> getHitStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
