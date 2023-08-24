package ru.practicum.ewm.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.validation.group.Create;
import ru.practicum.ewm.validation.group.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;

    @NotBlank(message = "Комментарий не может быть пустой.", groups = {Create.class, Update.class})
    @Size(max = 1000, message = "Максимальное допустимое количество символов комментария 1000.",
            groups = {Create.class, Update.class})
    String text;

    UserShortDto author;

    Long eventId;

    LocalDateTime created;

    Boolean edited;
}
