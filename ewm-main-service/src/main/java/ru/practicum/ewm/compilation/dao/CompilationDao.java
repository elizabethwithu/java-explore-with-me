package ru.practicum.ewm.compilation.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationDao extends JpaRepository<Compilation, Long> {
    List<Compilation> findByPinned(Boolean pinned, PageRequest pageRequest);
}
