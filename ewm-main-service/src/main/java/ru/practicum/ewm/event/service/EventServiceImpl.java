package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitClient;
import ru.practicum.ewm.HitInputDto;
import ru.practicum.ewm.HitOutputDto;
import ru.practicum.ewm.category.dao.CategoryDao;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.dao.LocationDao;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.Status;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UnsupportedException;
import ru.practicum.ewm.request.dao.RequestDao;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.dao.UserDao;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.utils.StatUtil;
import ru.practicum.ewm.utils.UnionService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.model.State.PUBLISHED;
import static ru.practicum.ewm.event.model.Status.CONFIRMED;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final UserDao userDao;
    private final LocationDao locationDao;
    private final CategoryDao categoryDao;
    private final RequestDao requestDao;
    private final HitClient hitClient;
    private final UnionService unionService;

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));

        Long categoryId = dto.getCategory();
        Category category = categoryDao.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category", categoryId));

        Location location = LocationMapper.toLocation(dto.getLocation());
        locationDao.save(location);

        if (dto.getPaid() == null) {
            dto.setPaid(false);
        }

        if (dto.getRequestModeration() == null) {
            dto.setRequestModeration(true);
        }

        if (dto.getParticipantLimit() == null) {
            dto.setParticipantLimit(0L);
        }

        Event savedEvent = eventDao.save(EventMapper.toEvent(dto, category, location, user));
        log.info("Событие {} успешно добавлено.", savedEvent.getId());

        return EventMapper.toEventFullDto(savedEvent, 0L);
    }

    @Override
    public List<EventShortDto> getAllInitiatorEvents(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Event> events = eventDao.findAllByInitiatorId(userId, pageRequest);
        log.info("Найден список из {} событий, опубликованных пользователем {}.", events.size(), userId);

        return EventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventFullDto getInitiatorEventById(Long userId, Long eventId) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("User is not the initiator of the event.");
        }
        List<HitOutputDto> hits = unionService.getViews(List.of(eventId));
        Map<Long, Long> views = StatUtil.mapHitsToViewCountByEventId(hits);

        log.info("Найдено событие {}, опубликованное пользователем {}.",eventId, userId);
        return EventMapper.toEventFullDto(event, views.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<RequestDto> findEventRequestsByInitiator(Long userId, Long eventId) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("User is not the initiator of the event.");
        }
        List<Request> requests = requestDao.findByEventId(eventId);

        log.info("Найдены запросы на участие в событии {}, опубликованном пользователем {}.",eventId, userId);
        return RequestMapper.toRequestDtoList(requests);
    }

    @Transactional
    @Override
    public EventFullDto updateInitiatorEvent(NewEventDto eventUpdateDto, Long userId, Long eventId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не является инициатором события %s.",userId, eventId));
        }

        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException(String.format("Пользователь %s не может изменить событие опубликованное событие %s",userId, eventId));
        }

        Event updatedEvent = updateEvent(event, eventUpdateDto);

        List<HitOutputDto> hits = unionService.getViews(List.of(eventId));
        Map<Long, Long> views = StatUtil.mapHitsToViewCountByEventId(hits);
        return EventMapper.toEventFullDto(updatedEvent, views.getOrDefault(event.getId(), 0L));
    }

    @Transactional
    @Override
    public RequestStatusUpdateResultDto updateStatusRequestsByUserId(RequestStatusUpdateRequestDto request,
                                                                     Long userId, Long eventId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));

        RequestStatusUpdateResultDto result = RequestStatusUpdateResultDto.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не является инициатором события %s.",userId, eventId));
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Exceeded the limit of participants");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        long vacantPlace = event.getParticipantLimit() - event.getConfirmedRequests();

        List<Request> requests = requestDao.findAllById(request.getRequestIds());

        for (Request nextRequest : requests) {
            if (!nextRequest.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Запрос должен иметь статус PENDING");
            }

            if (request.getStatus().equals(CONFIRMED) && vacantPlace > 0) {
                nextRequest.setStatus(CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
                confirmedRequests.add(nextRequest);
                vacantPlace--;
            } else {
                nextRequest.setStatus(Status.REJECTED);
                rejectedRequests.add(nextRequest);
            }
        }

        result.setConfirmedRequests(RequestMapper.toRequestDtoList(confirmedRequests));
        result.setRejectedRequests(RequestMapper.toRequestDtoList(rejectedRequests));

        return result;
    }

    @Override
    public EventFullDto findEventById(Long id, String uri, String ip) {
        Event event = eventDao.findById(id).orElseThrow(() -> new NotFoundException("Event", id));

        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException(String.format("Событие %s не опубликовано", id));
        }

        sendStats(uri, ip);
        List<HitOutputDto> hits = unionService.getViews(List.of(id));
        Map<Long, Long> views = StatUtil.mapHitsToViewCountByEventId(hits);

        return EventMapper.toEventFullDto(event, views.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Boolean onlyAvailable, String sort, Integer from,
                                             Integer size, String uri, String ip) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new UnsupportedException("Дата окончания раньше даты начала.");
            }
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Event> events = eventDao.findEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageRequest);
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        sendStats(uri, ip);

        List<HitOutputDto> hits = unionService.getViews(eventIds);
        Map<Long, Long> views = StatUtil.mapHitsToViewCountByEventId(hits);

        List<EventShortDto> result = EventMapper.toEventShortDtoList(events);

        for (EventShortDto event : result) {
            event.setViews(views.get(event.getId()));
        }

        return result;
    }

    @Override
    public List<EventFullDto> findAllEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventDao.findAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            eventIds.add(event.getId());
        }

        List<HitOutputDto> hits = unionService.getViews(eventIds);
        Map<Long, Long> views = StatUtil.mapHitsToViewCountByEventId(hits);
        List<EventFullDto> result = EventMapper.toEventFullDtoList(events);
        for (EventFullDto event : result) {
            event.setViews(views.getOrDefault(event.getId(), 0L));
        }
        return result;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, NewEventDto eventDto) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {

                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Event - %s, has already been published, cannot be published again ", event.getTitle()));
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(State.PUBLISHED);

            } else {

                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Event - %s, cannot be canceled because its statute is not \"PENDING\"", event.getTitle()));
                }
                event.setState(State.CANCELED);
            }
        }

        Event updateEvent = updateEvent(event, eventDto);
        List<HitOutputDto> hits = unionService.getViews(List.of(eventId));
        Map<Long, Long> views = StatUtil.mapHitsToViewCountByEventId(hits);

        return EventMapper.toEventFullDto(updateEvent, views.getOrDefault(event.getId(), 0L));
    }

    public static void checkEventAvailability(EventDao dao, Long id) {
        if (!dao.existsById(id)) {
            throw new NotFoundException("Event", id);
        }
    }

    private Event updateEvent(Event event, NewEventDto eventUpdateDto) {
        Long categoryId = eventUpdateDto.getCategory();
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (categoryId != null) {
            Category category = categoryDao.findById(categoryId).orElseThrow(
                    () -> new NotFoundException("Category", categoryId));
            event.setCategory(category);
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getEventDate() != null) {
            event.setEventDate(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(eventUpdateDto.getLocation()));
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventUpdateDto.getStateAction() == StateAction.REJECT_EVENT ||
                    eventUpdateDto.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            } else if (eventUpdateDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            }
        }
        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateDto.getTitle());
        }

        locationDao.save(event.getLocation());
        return eventDao.save(event);
    }

    private void sendStats(String uri, String ip) {
        HitInputDto hitDto = HitInputDto.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        hitClient.addHit(hitDto);
    }
}
