package com.typeforsheet.backend.domain.user.controller;

import com.typeforsheet.backend.domain.user.dto.request.UpdateNicknameRequest;
import com.typeforsheet.backend.domain.user.dto.response.UserProfileResponse;
import com.typeforsheet.backend.domain.user.service.UserService;
import com.typeforsheet.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이페이지", description = "MUBLE 사용자 정보 조회 및 닉네임 설정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "내 정보 조회",
      description =
          """
            로그인한 사용자의 정보를 조회합니다.

            - 유효한 MUBLE Access Token이 필요합니다.
            - JWT를 통해 로그인한 사용자를 식별합니다.
            - 사용자 ID와 닉네임을 반환합니다.
            - 닉네임을 설정하지 않은 경우 null로 반환됩니다.
            - 존재하지 않는 회원은 조회할 수 없습니다.
            """)
  @GetMapping
  public ApiResponse<UserProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {

    Long userId = Long.valueOf(jwt.getSubject());

    UserProfileResponse profile = userService.getMyProfile(userId);

    return ApiResponse.success("내 정보 조회에 성공했습니다.", profile);
  }

  @Operation(
      summary = "닉네임 설정 및 수정",
      description =
          """
            로그인한 사용자의 닉네임을 설정하거나 수정합니다.

            - 유효한 MUBLE Access Token이 필요합니다.
            - 최초 닉네임 설정과 기존 닉네임 변경에 모두 사용합니다.
            - 닉네임의 앞뒤 공백은 제거됩니다.
            - 빈 닉네임이나 50자를 초과하는 닉네임은 허용하지 않습니다.
            - 변경된 사용자 정보를 반환합니다.
            - 닉네임 세부 정책은 추후 기획 확정에 따라 변경될 수 있습니다.
            """)
  @PatchMapping("/nickname")
  public ApiResponse<UserProfileResponse> updateNickname(
      @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateNicknameRequest request) {

    Long userId = Long.valueOf(jwt.getSubject());

    UserProfileResponse profile = userService.updateNickname(userId, request.nickname());

    return ApiResponse.success("닉네임 설정 및 수정에 성공했습니다.", profile);
  }
}
