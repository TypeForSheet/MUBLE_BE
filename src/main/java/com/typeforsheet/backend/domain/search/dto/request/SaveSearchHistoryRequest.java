package com.typeforsheet.backend.domain.search.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SaveSearchHistoryRequest(
    @Schema(description = "검색창에 입력한 검색어 또는 미리보기에서 선택한 항목의 이름", example = "아이유", maxLength = 255)
        String keyword) {}
