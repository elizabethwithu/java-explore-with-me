package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.validation.group.Create;
import ru.practicum.ewm.validation.group.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationInputDto {
    Set<Long> events = new HashSet<>();

    Boolean pinned;

    @Size(max = 50, message = "Длина заголовка подборки превышает 50 символов.", groups = {Create.class, Update.class})
    @NotBlank(message = "Наименование подборки мероприятий не указано.", groups = Create.class)
    String title;
}
