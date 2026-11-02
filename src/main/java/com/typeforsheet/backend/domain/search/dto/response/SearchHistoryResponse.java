package com.typeforsheet.backend.domain.search.dto.response;

import com.typeforsheet.backend.domain.search.entity.SearchHistory;
import java.time.LocalDateTime;

public record SearchHistoryResponse(Long id, String keyword, LocalDateTime searchedAt) {

  public static SearchHistoryResponse from(SearchHistory history) {
    return new SearchHistoryResponse(
        history.getId(), history.getKeyword(), history.getSearchedAt());
  }
}
