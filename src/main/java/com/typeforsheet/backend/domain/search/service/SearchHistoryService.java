package com.typeforsheet.backend.domain.search.service;

import com.typeforsheet.backend.domain.search.dto.response.SearchHistoryResponse;
import com.typeforsheet.backend.domain.search.entity.SearchHistory;
import com.typeforsheet.backend.domain.search.repository.SearchHistoryRepository;
import com.typeforsheet.backend.domain.user.entity.User;
import com.typeforsheet.backend.domain.user.repository.UserRepository;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

  private static final int MAX_HISTORY_COUNT = 10;
  private static final int MAX_KEYWORD_LENGTH = 255;

  private final SearchHistoryRepository searchHistoryRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<SearchHistoryResponse> getHistories(Long userId) {

    if (!userRepository.existsById(userId)) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    return searchHistoryRepository.findByUser_IdOrderBySearchedAtDescIdDesc(userId).stream()
        .limit(MAX_HISTORY_COUNT)
        .map(SearchHistoryResponse::from)
        .toList();
  }

  @Transactional
  public SearchHistoryResponse saveHistory(Long userId, String keyword) {

    String normalizedKeyword = normalizeKeyword(keyword);

    User user =
        userRepository
            .findByIdForUpdate(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    SearchHistory history =
        searchHistoryRepository
            .findByUser_IdAndKeyword(userId, normalizedKeyword)
            .map(
                existing -> {
                  existing.refreshSearch(normalizedKeyword);
                  return existing;
                })
            .orElseGet(
                () -> searchHistoryRepository.save(new SearchHistory(user, normalizedKeyword)));

    searchHistoryRepository.flush();

    List<SearchHistory> histories =
        searchHistoryRepository.findByUser_IdOrderBySearchedAtDescIdDesc(userId);

    if (histories.size() > MAX_HISTORY_COUNT) {
      List<SearchHistory> oldHistories = histories.subList(MAX_HISTORY_COUNT, histories.size());

      searchHistoryRepository.deleteAll(oldHistories);
    }

    return SearchHistoryResponse.from(history);
  }

  @Transactional
  public void deleteHistory(Long userId, Long historyId) {

    if (historyId == null || historyId <= 0) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    userRepository
        .findByIdForUpdate(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    searchHistoryRepository
        .findByIdAndUser_Id(historyId, userId)
        .ifPresent(searchHistoryRepository::delete);
  }

  private String normalizeKeyword(String keyword) {

    if (keyword == null) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    String normalized = keyword.strip();

    if (normalized.isEmpty()
        || normalized.length() > MAX_KEYWORD_LENGTH
        || normalized.codePoints().allMatch(Character::isISOControl)) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    return normalized;
  }
}
