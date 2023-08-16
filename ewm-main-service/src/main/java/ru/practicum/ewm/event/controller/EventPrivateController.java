package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Positive @PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        return eventService.createEvent(userId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllInitiatorEvents(@Positive @PathVariable Long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllInitiatorEvents(userId, from, size);
    }

    @GetMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getInitiatorEventById(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        return eventService.getInitiatorEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateInitiatorEvent(@RequestBody @Valid UpdateEventDto eventUpdateDto,
                                             @Positive @PathVariable Long userId,
                                             @Positive @PathVariable Long eventId) {

        return eventService.updateInitiatorEvent(eventUpdateDto, userId, eventId);
    }

    @GetMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findEventRequestsByInitiator(@Positive @PathVariable Long userId,
                                                         @Positive @PathVariable Long eventId) {
        return eventService.findEventRequestsByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusUpdateResult updateStatusRequestsByUserId(@RequestBody RequestStatusUpdateRequest request,
                                                                  @Positive @PathVariable Long userId,
                                                                  @Positive @PathVariable Long eventId) {
        return eventService.updateStatusRequestsByUserId(request, userId, eventId);
    }
}
