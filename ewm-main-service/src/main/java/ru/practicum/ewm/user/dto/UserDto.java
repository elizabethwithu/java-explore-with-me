package ru.practicum.ewm.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;

    @NotBlank(message = "Почтовый адрес пустой.")
    @Email(message = "Почтовый адрес не соответствует требованиям.")
    @Size(min = 6, max = 254, message = "Почтовый адрес должен содержать от 6 до 254 символов.")
    String email;

    @NotBlank(message = "Имя пользователя не указано.")
    @Size(min = 2, max = 250, message = "Имя должно содержать от 2 до 250 символов.")
    String name;
}
