package ru.practicum.ewm.stats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.HitOutputDto;
import ru.practicum.ewm.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitDao extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.ewm.stats.HitOutputDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.uri, h.app  " +
            "ORDER BY count(h.ip) desc")
    List<HitOutputDto> findAllByTimestampAndUrisAndUniqueIp(@Param("start") LocalDateTime start,
                                                            @Param("end") LocalDateTime end,
                                                            @Param("uris") List<String> uris);


    @Query("SELECT new ru.practicum.ewm.stats.HitOutputDto(h.app, h.uri, count(h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.uri, h.app  " +
            "ORDER BY count(h.ip) desc")
    List<HitOutputDto> findAllByTimestampAndUris(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.stats.HitOutputDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.uri, h.app  " +
            "ORDER BY count(h.ip) desc")
    List<HitOutputDto> findAllByTimestampAndUniqueIp(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);


    @Query("SELECT new ru.practicum.ewm.stats.HitOutputDto(h.app, h.uri, count(h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.uri, h.app " +
            "ORDER BY count(h.ip) desc")
    List<HitOutputDto> findAllByTimestamp(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);
}
