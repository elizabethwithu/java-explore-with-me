package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.DateTimeFormat.DATE_TIME_FORMATTER;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Long views;
}
