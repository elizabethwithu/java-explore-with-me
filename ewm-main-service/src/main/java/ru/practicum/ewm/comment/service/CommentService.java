package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, CommentDto commentDto);

    CommentDto updateComment(Long userId, Long commentId, Long eventId, CommentDto commentDto);

    void removeCommentByUser(Long userId, Long eventId, Long commentId);

    CommentDto findCommentById(Long userId, Long eventId, Long commentId);

    List<CommentDto> findAll(Long userId, Long eventId);

    List<CommentDto> findCommentByText(Long userId, Long eventId, String text, Integer from, Integer size);

    void removeCommentByAdmin(Long commentId);

    List<CommentDto> findAllByAdmin(LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<CommentDto> findAllByEventId(Long eventId, Integer from, Integer size);
}
