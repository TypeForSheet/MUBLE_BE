package com.typeforsheet.backend.domain.search.controller;

import com.typeforsheet.backend.domain.search.dto.request.SaveSearchHistoryRequest;
import com.typeforsheet.backend.domain.search.dto.response.SearchHistoryResponse;
import com.typeforsheet.backend.domain.search.service.SearchHistoryService;
import com.typeforsheet.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "검색하기", description = "MUBLE 최근 검색어 조회, 저장 및 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/histories")
@SecurityRequirement(name = "bearerAuth")
public class SearchHistoryController {

  private final SearchHistoryService searchHistoryService;

  @Operation(
      summary = "최근 검색어 목록 조회",
      description =
          """
          로그인한 사용자의 최근 검색어 목록을 조회합니다.

          - 유효한 MUBLE Access Token이 필요합니다.
          - 사용자별 검색 기록을 최신순으로 조회합니다.
          - 최근 검색어는 최대 10개까지 반환합니다.
          - 저장된 검색 기록이 없다면 빈 배열을 반환합니다.
          - 다른 사용자의 검색 기록은 조회할 수 없습니다.
          """)
  @GetMapping
  public ApiResponse<List<SearchHistoryResponse>> getHistories(@AuthenticationPrincipal Jwt jwt) {

    Long userId = Long.valueOf(jwt.getSubject());

    List<SearchHistoryResponse> histories = searchHistoryService.getHistories(userId);

    return ApiResponse.success("최근 검색어 조회에 성공했습니다.", histories);
  }

  @Operation(
      summary = "최근 검색어 저장",
      description =
          """
          사용자가 검색을 실행하거나 검색 미리보기 결과를 선택했을 때
          최근 검색어를 저장합니다.

          - 유효한 MUBLE Access Token이 필요합니다.
          - 검색 버튼 클릭 시 입력한 텍스트를 저장합니다.
          - 미리보기 결과 선택 시 선택한 항목의 이름을 저장합니다.
          - 동일한 검색어가 존재하면 중복 저장하지 않고 검색 시각을 갱신합니다.
          - 최근 검색어는 최신순으로 최대 10개까지 유지합니다.
          - 10개를 초과하면 가장 오래된 검색 기록을 삭제합니다.
          - 검색어의 앞뒤 공백은 제거됩니다.
          - 빈 검색어나 255자를 초과하는 검색어는 저장할 수 없습니다.
          """)
  @PostMapping
  public ApiResponse<SearchHistoryResponse> saveHistory(
      @AuthenticationPrincipal Jwt jwt, @RequestBody SaveSearchHistoryRequest request) {

    Long userId = Long.valueOf(jwt.getSubject());

    SearchHistoryResponse history = searchHistoryService.saveHistory(userId, request.keyword());

    return ApiResponse.success("최근 검색어 저장에 성공했습니다.", history);
  }

  @Operation(
      summary = "최근 검색어 개별 삭제",
      description =
          """
          로그인한 사용자의 특정 검색 기록을 삭제합니다.

          - 유효한 MUBLE Access Token이 필요합니다.
          - 삭제할 검색 기록의 ID를 경로에 전달합니다.
          - 본인의 검색 기록만 삭제할 수 있습니다.
          - 존재하지 않거나 본인 소유가 아닌 기록은 삭제되지 않습니다.
          - 삭제 대상이 없어도 정상 처리됩니다.
          """)
  @DeleteMapping("/{historyId}")
  public ApiResponse<Void> deleteHistory(
      @AuthenticationPrincipal Jwt jwt,
      @Parameter(description = "삭제할 최근 검색 기록 ID", example = "1") @PathVariable Long historyId) {

    Long userId = Long.valueOf(jwt.getSubject());

    searchHistoryService.deleteHistory(userId, historyId);

    return ApiResponse.success("최근 검색어 삭제에 성공했습니다.");
  }
}
