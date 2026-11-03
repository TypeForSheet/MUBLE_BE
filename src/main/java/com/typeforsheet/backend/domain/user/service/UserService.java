package com.typeforsheet.backend.domain.user.service;

import com.typeforsheet.backend.domain.user.dto.response.UserProfileResponse;
import com.typeforsheet.backend.domain.user.entity.User;
import com.typeforsheet.backend.domain.user.repository.UserRepository;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public UserProfileResponse getMyProfile(Long userId) {

    User user = findUser(userId);

    return UserProfileResponse.from(user);
  }

  @Transactional
  public UserProfileResponse updateNickname(Long userId, String nickname) {

    String normalizedNickname = normalizeNickname(nickname);

    User user = findUser(userId);

    user.updateNickname(normalizedNickname);

    return UserProfileResponse.from(user);
  }

  private User findUser(Long userId) {

    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }

  private String normalizeNickname(String nickname) {

    if (nickname == null) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    String normalized = nickname.strip();

    if (normalized.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    int length = normalized.codePointCount(0, normalized.length());

    if (length > 50) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    if (normalized.codePoints().anyMatch(Character::isISOControl)) {
      throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }

    return normalized;
  }
}
