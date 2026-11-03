package com.typeforsheet.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateNicknameRequest(
    @Schema(
            description = "설정하거나 변경할 닉네임입니다. 앞뒤 공백 제거 후 1자 이상 50자 이하여야 합니다.",
            example = "Rilla",
            minLength = 1)
        String nickname) {}
