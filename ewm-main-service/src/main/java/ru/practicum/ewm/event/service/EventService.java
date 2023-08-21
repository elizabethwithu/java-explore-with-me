package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getAllInitiatorEvents(Long userId, Integer from, Integer size);

    EventFullDto getInitiatorEventById(Long userId, Long eventId);

    List<RequestDto> findEventRequestsByInitiator(Long userId, Long eventId);

    EventFullDto updateInitiatorEvent(NewEventDto eventUpdateDto, Long userId, Long eventId);

    RequestStatusUpdateResultDto updateStatusRequestsByUserId(RequestStatusUpdateRequestDto request, Long userId, Long eventId);

    EventFullDto findEventById(Long id, String uri, String ip);

    List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Boolean onlyAvailable, String sort, Integer from,
                                      Integer size, String uri, String ip);

    List<EventFullDto> findAllEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, NewEventDto eventDto);
}
