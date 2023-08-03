package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitInputDto {
    Long id;

    @Size(max = 64, message = "app не может быть длиннее 64 символов.")
    @NotBlank(message = "app отсутствует.")
    String app;

    @Size(max = 128, message = "uri не может быть длиннее 128 символов.")
    @NotBlank(message = "uri отсутствует.")
    String uri;

    @Size(max = 64, message = "ip не может быть длиннее 64 символов.")
    @NotBlank(message = "ip отсутствует.")
    String ip;

    @NotNull(message = "timestamp отсутствует.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
