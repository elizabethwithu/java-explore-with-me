package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.validation.EventDate;
import ru.practicum.ewm.validation.group.Create;
import ru.practicum.ewm.validation.group.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.DateTimeFormat.DATE_TIME_FORMATTER;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank(message = "Аннотация не может быть пустой или содержать пробелы.", groups = Create.class)
    @Size(min = 20, max = 2000, message = "Количество символов аннотации должно быть больше 20, но меньше 2000.",
            groups = {Create.class, Update.class})
    String annotation;

    @NotNull(message = "Айди категории не указан.", groups = Create.class)
    Long category;

    @NotBlank(message = "Описание не может быть пустым или содержать пробелы.", groups = Create.class)
    @Size(min = 20, max = 7000, message = "Количество символов описания должно быть больше 20, но меньше 7000.",
            groups = {Create.class, Update.class})
    String description;

    @NotNull(message = "Дата события не указана.", groups = Create.class)
    @EventDate(message = "Время события не может быть раньше, чем через два часа от текущего момента.",
            groups = Create.class)
    @Future(message = "Время события указано в прошлом.", groups = Update.class)
    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime eventDate;

    @NotNull(message = "Локация не указана.", groups = Create.class)
    LocationDto location;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Заголовок события не может быть пустым или содержать пробелы.", groups = Create.class)
    @Size(min = 3, max = 120, message = "Количество символов заголовка должно быть больше 3, но меньше 120.",
            groups = {Create.class, Update.class})
    String title;

    StateAction stateAction;
}
