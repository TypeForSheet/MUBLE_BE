package com.typeforsheet.backend.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.typeforsheet.backend.domain.user.dto.response.UserProfileResponse;
import com.typeforsheet.backend.domain.user.entity.User;
import com.typeforsheet.backend.domain.user.repository.UserRepository;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("기존닉네임");
    ReflectionTestUtils.setField(user, "id", 1L);
  }

  @Test
  void getsMyProfile() {

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserProfileResponse response = userService.getMyProfile(1L);

    assertEquals(1L, response.id());
    assertEquals("기존닉네임", response.nickname());

    verify(userRepository).findById(1L);
  }

  @Test
  void getsProfileWithNullNickname() {

    User newUser = new User(null);
    ReflectionTestUtils.setField(newUser, "id", 2L);

    when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));

    UserProfileResponse response = userService.getMyProfile(2L);

    assertEquals(2L, response.id());
    assertNull(response.nickname());
  }

  @Test
  void setsInitialNickname() {

    User newUser = new User(null);
    ReflectionTestUtils.setField(newUser, "id", 2L);

    when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));

    UserProfileResponse response = userService.updateNickname(2L, "Rilla");

    assertEquals(2L, response.id());
    assertEquals("Rilla", response.nickname());
    assertEquals("Rilla", newUser.getNickname());
  }

  @Test
  void updatesNickname() {

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserProfileResponse response = userService.updateNickname(1L, "새닉네임");

    assertEquals(1L, response.id());
    assertEquals("새닉네임", response.nickname());
    assertEquals("새닉네임", user.getNickname());

    verify(userRepository).findById(1L);
  }

  @Test
  void removesLeadingAndTrailingWhitespace() {

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserProfileResponse response = userService.updateNickname(1L, "  Rilla  ");

    assertEquals("Rilla", response.nickname());
    assertEquals("Rilla", user.getNickname());
  }

  @Test
  void allowsNicknameWithExactly50Characters() {

    String nickname = "가".repeat(50);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserProfileResponse response = userService.updateNickname(1L, nickname);

    assertEquals(nickname, response.nickname());
    assertEquals(50, response.nickname().length());
  }

  @Test
  void rejectsNullNickname() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, null));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsEmptyNickname() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, ""));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsWhitespaceOnlyNickname() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, "   "));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsUnicodeWhitespaceOnlyNickname() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, "\u3000\u3000"));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsNicknameLongerThan50Characters() {

    String nickname = "가".repeat(51);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, nickname));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsNicknameContainingControlCharacter() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, "Rilla\nTest"));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsNicknameContainingISOControlCharacter() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, "\u0007Rilla"));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void rejectsNonexistentUserWhenGettingProfile() {

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.getMyProfile(99L));

    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void rejectsNonexistentUserWhenUpdatingNickname() {

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(99L, "Rilla"));

    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void rejectsNonBreakingSpaceOnlyNickname() {

    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.updateNickname(1L, "\u00A0\u00A0"));

    assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

    verifyNoInteractions(userRepository);
  }

  @Test
  void allowsNicknameContainingTextAndNonBreakingSpace() {

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserProfileResponse response = userService.updateNickname(1L, "Rilla\u00A0");

    assertEquals("Rilla\u00A0", response.nickname());
    assertEquals("Rilla\u00A0", user.getNickname());
  }
}
