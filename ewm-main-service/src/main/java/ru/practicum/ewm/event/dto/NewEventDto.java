package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.location.LocationDto;
import ru.practicum.ewm.utils.validation.EventDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.constants.DateTimeFormat.DATE_TIME_FORMATTER;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank(message = "Аннотация не может быть пустой или содержать пробелы.")
    @Size(min = 20, max = 2000, message = "Количество символов аннотации должно быть больше 20, но меньше 2000.")
    String annotation;

    @NotNull(message = "Айди категории не указан.")
    Long category;

    @NotBlank(message = "Описание не может быть пустым или содержать пробелы.")
    @Size(min = 20, max = 7000, message = "Количество символов описания должно быть больше 20, но меньше 7000.")
    String description;

    @NotNull(message = "Дата события не указана.")
    @EventDate(message = "Время события не может быть раньше, чем через два часа от текущего момента.")
    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime eventDate;

    @NotNull(message = "Локация не указана.")
    LocationDto location;

    @Builder.Default
    Boolean paid = false;

    Long participantLimit;

    @Builder.Default
    Boolean requestModeration = true;

    @NotBlank(message = "Заголовок события не может быть пустым или содержать пробелы.")
    @Size(min = 3, max = 120, message = "Количество символов заголовка должно быть больше 3, но меньше 120.")
    String title;
}
