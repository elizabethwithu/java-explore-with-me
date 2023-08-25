package ru.practicum.ewm.comment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dao.CommentDao;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UnsupportedException;
import ru.practicum.ewm.user.dao.UserDao;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.service.AbstractServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl extends AbstractServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final EventDao eventDao;

    public CommentServiceImpl(CommentDao commentDao, UserDao userDao, EventDao eventDao) {
        super(userDao);
        this.commentDao = commentDao;
        this.eventDao = eventDao;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));

        Comment savedComment = commentDao.save(CommentMapper.toComment(commentDto, user, event));
        log.info("Комментарий {} успешно добавлен.", savedComment.getId());

        return CommentMapper.toCommentDto(savedComment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long commentId, Long eventId, CommentDto commentDto) {
        checkUserAvailability(userId);
        checkEventAvailability(eventId);

        Comment comment = commentDao.findById(commentId).orElseThrow(() -> new NotFoundException("Comment", commentId));
        checkAuthorComment(userId, comment);

        comment.setText(commentDto.getText());
        comment.setEdited(true);
        Comment savedComment = commentDao.save(comment);

        log.info("Комментарий {} успешно обновлен.", savedComment.getId());
        return CommentMapper.toCommentDto(savedComment);

    }

    @Transactional
    @Override
    public void removeCommentByUser(Long userId, Long eventId, Long commentId) {
        checkUserAvailability(userId);
        checkEventAvailability(eventId);

        Comment comment = commentDao.findById(commentId).orElseThrow(() -> new NotFoundException("Comment", commentId));
        checkAuthorComment(userId, comment);

        commentDao.deleteById(commentId);
        log.info("Комментарий {} успешно удален.", commentId);
    }

    @Transactional
    @Override
    public void removeCommentByAdmin(Long commentId) {
        if (!commentDao.existsById(commentId)) {
            throw new NotFoundException("Comment", commentId);
        }

        commentDao.deleteById(commentId);
        log.info("Комментарий {} успешно удален администратором.", commentId);
    }

    @Override
    public CommentDto findCommentById(Long userId, Long eventId, Long commentId) {
        checkUserAvailability(userId);
        checkEventAvailability(eventId);

        Comment comment = commentDao.findByIdAndAuthorIdAndEventId(commentId, userId, eventId)
                .orElseThrow(() -> new NotFoundException("Comment", commentId));

        log.info("Комментарий {} успешно найден.", commentId);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findAll(Long userId, Long eventId) {
        checkUserAvailability(userId);
        checkEventAvailability(eventId);

        List<Comment> comments = commentDao.findAllByAuthorIdAndEventId(userId, eventId);
        log.info("Получен список из {} комментариев.", comments.size());

        return CommentMapper.toCommentDtoList(comments);
    }

    @Override
    public List<CommentDto> findCommentByText(Long userId, Long eventId, String text, Integer from, Integer size) {
        checkUserAvailability(userId);
        checkEventAvailability(eventId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Comment> comments = commentDao.findAllByAuthorIdAndEventIdAndTextContainingIgnoreCase(userId, eventId,
                                                                                                    text, pageRequest);
        log.info("Получен список из {} комментариев.", comments.size());

        return CommentMapper.toCommentDtoList(comments);
    }

    @Override
    public List<CommentDto> findAllByAdmin(LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments;

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new UnsupportedException("Дата окончания раньше даты начала.");
            }
            comments = commentDao.findByCreatedBetween(rangeStart, rangeEnd, pageRequest);
        } else {
            comments = commentDao.findAll();
        }

        log.info("Получен список из {} комментариев.", comments.size());
        return CommentMapper.toCommentDtoList(comments);
    }

    public List<CommentDto> findAllByEventId(Long eventId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentDao.findByEventId(eventId, pageRequest);
        log.info("Получен список из {} комментариев.", comments.size());

        return CommentMapper.toCommentDtoList(comments);

    }

    private static void checkAuthorComment(Long userId, Comment comment) {
        if (!Objects.equals(userId, comment.getAuthor().getId())) {
            throw new ConflictException("Изменение комментария доступно только автору.");
        }
    }

    private void checkEventAvailability(Long eventId) {
        if (!eventDao.existsById(eventId)) {
            throw new NotFoundException("Event", eventId);
        }
    }
}
