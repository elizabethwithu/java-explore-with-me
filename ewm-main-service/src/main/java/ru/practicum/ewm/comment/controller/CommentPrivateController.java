package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.validation.group.Create;
import ru.practicum.ewm.validation.group.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId, @PathVariable Long eventId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return commentService.addComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long commentId, @PathVariable Long eventId,
                                    @Validated(Update.class) @RequestBody CommentDto commentDto) {
        return commentService.updateComment(userId, eventId, commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                    @PathVariable Long commentId) {
        commentService.removeCommentByUser(userId, eventId, commentId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto findCommentById(@PathVariable Long userId, @PathVariable Long eventId,
                                      @PathVariable Long commentId) {
        return commentService.findCommentById(userId, eventId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAll(@PathVariable Long userId, @PathVariable Long eventId) {
        return commentService.findAll(userId, eventId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findCommentByText(@PathVariable Long userId, @PathVariable Long eventId,
                                              @RequestParam @NotBlank String text,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.findCommentByText(userId, eventId, text, from, size);
    }
}
