package com.typeforsheet.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateNicknameRequest(
    @Schema(description = "설정하거나 변경할 닉네임", example = "Rilla", minLength = 1, maxLength = 50)
        String nickname) {}
