package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.HitInputDto;
import ru.practicum.ewm.HitOutputDto;
import ru.practicum.ewm.stats.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody HitInputDto hitInputDto) {
        hitService.addHit(hitInputDto);
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<HitOutputDto> getHitStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT)LocalDateTime end,
                                          @RequestParam(required = false) List<String> uris,
                                          @RequestParam(required = false) boolean unique) {
        return hitService.getHitStats(start, end, uris, unique);
    }
}
