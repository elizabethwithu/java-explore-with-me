package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.DateTimeFormat.DATE_TIME_FORMATTER;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    LocationDto location;

    Boolean paid;

    Long participantLimit;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime publishedOn;

    Boolean requestModeration;

    State state;

    String title;

    Long views;
}
