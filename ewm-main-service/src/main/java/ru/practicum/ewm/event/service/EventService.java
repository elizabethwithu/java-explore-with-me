package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.utils.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto dto);

    List<EventShortDto> getAllInitiatorEvents(Long userId, Integer from, Integer size);

    EventFullDto getInitiatorEventById(Long userId, Long eventId);

    List<RequestDto> findEventRequestsByInitiator(Long userId, Long eventId);

    EventFullDto updateInitiatorEvent(UpdateEventDto eventUpdateDto, Long userId, Long eventId);

    RequestStatusUpdateResult updateStatusRequestsByUserId(RequestStatusUpdateRequest request, Long userId, Long eventId);

    EventFullDto findEventById(Long id, HttpServletRequest request);

    List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Boolean onlyAvailable, String sort, Integer from,
                                      Integer size, HttpServletRequest request);

    List<EventFullDto> findAllEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto eventDto);
}
