package com.typeforsheet.backend.domain.search.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.typeforsheet.backend.domain.search.dto.response.SearchHistoryResponse;
import com.typeforsheet.backend.domain.search.entity.SearchHistory;
import com.typeforsheet.backend.domain.search.repository.SearchHistoryRepository;
import com.typeforsheet.backend.domain.user.entity.User;
import com.typeforsheet.backend.domain.user.repository.UserRepository;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchHistoryServiceTest {

  private final SearchHistoryRepository searchHistoryRepository =
      mock(SearchHistoryRepository.class);

  private final UserRepository userRepository = mock(UserRepository.class);

  private final SearchHistoryService service =
      new SearchHistoryService(searchHistoryRepository, userRepository);

  @Test
  void rejectsUnicodeWhitespaceOnlyKeyword() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.saveHistory(1L, "\u2003"));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(searchHistoryRepository, userRepository);
  }

  @Test
  void removesLeadingAndTrailingUnicodeWhitespace() {

    User user = new User("테스터");

    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    when(searchHistoryRepository.findByUser_IdAndKeyword(1L, "아이유")).thenReturn(Optional.empty());

    when(searchHistoryRepository.save(any(SearchHistory.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    SearchHistoryResponse response = service.saveHistory(1L, "\u2003아이유\u2003");

    assertEquals("아이유", response.keyword());

    verify(searchHistoryRepository).findByUser_IdAndKeyword(1L, "아이유");
  }

  @Test
  void updatesKeywordCasingWhenExistingHistoryIsFound() {

    User user = new User("테스터");
    SearchHistory existing = new SearchHistory(user, "love");

    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    when(searchHistoryRepository.findByUser_IdAndKeyword(1L, "LOVE"))
        .thenReturn(Optional.of(existing));

    when(searchHistoryRepository.findByUser_IdOrderBySearchedAtDescIdDesc(1L))
        .thenReturn(List.of(existing));

    SearchHistoryResponse response = service.saveHistory(1L, "LOVE");

    assertEquals("LOVE", response.keyword());
    assertEquals("LOVE", existing.getKeyword());

    verify(searchHistoryRepository, never()).save(any(SearchHistory.class));
  }

  @Test
  void rejectsControlOnlyKeyword() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.saveHistory(1L, "\u0000\u0007"));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(searchHistoryRepository, userRepository);
  }

  @Test
  void allowsKeywordContainingTextAndControlCharacter() {

    User user = new User("테스터");

    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    when(searchHistoryRepository.findByUser_IdAndKeyword(1L, "\u0007Love"))
        .thenReturn(Optional.empty());

    when(searchHistoryRepository.save(any(SearchHistory.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(searchHistoryRepository.findByUser_IdOrderBySearchedAtDescIdDesc(1L))
        .thenReturn(List.of());

    SearchHistoryResponse response = service.saveHistory(1L, "\u0007Love");

    assertEquals("\u0007Love", response.keyword());

    verify(searchHistoryRepository).findByUser_IdAndKeyword(1L, "\u0007Love");
  }
}
