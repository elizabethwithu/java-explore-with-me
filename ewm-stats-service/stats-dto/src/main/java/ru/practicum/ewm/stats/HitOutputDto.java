package ru.practicum.ewm.stats;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitOutputDto {
    String app;

    String uri;

    Long hits;
}
