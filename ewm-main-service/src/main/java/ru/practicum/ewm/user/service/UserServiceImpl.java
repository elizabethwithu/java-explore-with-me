package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dao.UserDao;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Transactional
    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toUser(dto);
        User savedUser = userDao.save(user);
        log.info("Создан пользователь {}.", savedUser);

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public List<UserDto> findAllUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (ids == null) {
            Page<User> users = userDao.findAll(pageRequest);
            log.info("Список пользователей успешно получен.");
            return UserMapper.toUserDtoList(users);
        } else {
            List<User> userList = userDao.findByIdInOrderByIdAsc(ids, pageRequest);
            log.info("Список пользователей по идентификатору успешно получен.");
            return UserMapper.toUserDtoList(userList);
        }
    }

    @Transactional
    @Override
    public void removeUserById(Long id) {
        checkUserAvailability(userDao, id);
        userDao.deleteById(id);
        log.info("Пользователь {} успешно удален.", id);
    }

    public static void checkUserAvailability(UserDao dao, Long id) {
        if (!dao.existsById(id)) {
            throw new NotFoundException("User", id);
        }
    }
}
