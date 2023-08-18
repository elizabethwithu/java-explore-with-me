package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getLat(),
                location.getLon()
        );
    }

    public static Location toLocation(LocationDto locationDto) {
        return new Location(
                locationDto.getId(),
                locationDto.getLat(),
                locationDto.getLon()
        );
    }
}
