package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dao.UserDao;

@AllArgsConstructor
public abstract class AbstractServiceImpl {
    protected final UserDao userDao;

    protected void checkUserAvailability(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }
}