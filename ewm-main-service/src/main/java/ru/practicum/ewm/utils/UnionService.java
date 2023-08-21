package ru.practicum.ewm.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitClient;
import ru.practicum.ewm.HitOutputDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.utils.DateTimeFormat.START;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UnionService {
    private final HitClient hitClient;

    public List<HitOutputDto> getViews(List<Long> eventIds) {
        List<String> uris = StatUtil.makeUrisWithEventIds(eventIds);

        List<HitOutputDto> response = hitClient.getHitStats(START, LocalDateTime.now(), uris, true);
        log.info("Отправлен запрос на получение статистики {}.", uris);

        return response;
    }
}
