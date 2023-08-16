package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto dto);

    List<UserDto> findAllUsers(List<Long> ids, Integer from, Integer size);

    void removeUserById(Long id);
}
