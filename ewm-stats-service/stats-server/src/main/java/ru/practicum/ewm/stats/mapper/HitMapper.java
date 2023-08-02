package ru.practicum.ewm.stats.mapper;

import ru.practicum.ewm.stats.HitInputDto;
import ru.practicum.ewm.stats.model.Hit;

public class HitMapper {
    public static Hit toHit(HitInputDto hitInputDto) {
        return new Hit(
                hitInputDto.getId(),
                hitInputDto.getApp(),
                hitInputDto.getUri(),
                hitInputDto.getIp(),
                hitInputDto.getTimestamp()
        );
    }

    public HitInputDto toHitDto(Hit hit) {
        return new HitInputDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}
