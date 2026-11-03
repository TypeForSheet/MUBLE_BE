package com.typeforsheet.backend.domain.search.repository;

import com.typeforsheet.backend.domain.search.entity.SearchHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

  List<SearchHistory> findByUser_IdOrderBySearchedAtDescIdDesc(Long userId);

  Optional<SearchHistory> findByUser_IdAndKeyword(Long userId, String keyword);

  Optional<SearchHistory> findByIdAndUser_Id(Long id, Long userId);
}
