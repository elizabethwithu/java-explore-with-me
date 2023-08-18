package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.Status;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dao.RequestDao;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.dao.UserDao;
import ru.practicum.ewm.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.user.service.UserServiceImpl.checkUserAvailability;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestDao requestDao;

    private final UserDao userDao;

    private final EventDao eventDao;

    private final EntityManager em;

    @Transactional
    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));
        lockEvent(event);

        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %d является инициатором события %d.",userId, eventId));
        }
        if (event.getParticipantLimit() <= event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("Количество запросов к событию %d превышает лимит.", eventId));
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(String.format("Событие %d ещё не опубликовано.", eventId));
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(Status.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
            request = requestDao.save(request);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
            eventDao.save(event);
            unlockEvent(event);

            return RequestMapper.toRequestDto(request);
        }

        request = requestDao.save(request);
        log.info("Пользователь {} успешно отправил запрос на участие в мероприятии {}.", user.getName(), eventId);
        return RequestMapper.toRequestDto(request);
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkUserAvailability(userDao, userId);
        Request request = requestDao.findById(requestId).orElseThrow(() -> new NotFoundException("Request", requestId));

        request.setStatus(Status.CANCELED);
        Request savedRequest = requestDao.save(request);
        log.info("Пользователь {} отменил заявку {}.", userId, requestId);

        return RequestMapper.toRequestDto(savedRequest);
    }

    @Override
    public List<RequestDto> findRequestsByUserId(Long userId) {
        checkUserAvailability(userDao, userId);

        List<Request> requests = requestDao.findByRequesterId(userId);
        log.info("Найдены события для пользователя {}.", userId);

        return RequestMapper.toRequestDtoList(requests);
    }

    private void lockEvent(Event event) {
        em.lock(event, LockModeType.PESSIMISTIC_READ);
    }

    private void unlockEvent(Event event) {
        em.lock(event, LockModeType.NONE);
    }
}
