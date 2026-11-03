package com.typeforsheet.backend.domain.user.dto.response;

import com.typeforsheet.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileResponse(
    @Schema(description = "사용자 ID", example = "1") Long id,
    @Schema(description = "닉네임", example = "Rilla", nullable = true) String nickname) {

  public static UserProfileResponse from(User user) {
    return new UserProfileResponse(user.getId(), user.getNickname());
  }
}
