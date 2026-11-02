package com.typeforsheet.backend.domain.search.entity;

import com.typeforsheet.backend.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "search_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 255)
  private String keyword;

  @Column(name = "searched_at", nullable = false)
  private LocalDateTime searchedAt;

  public SearchHistory(User user, String keyword) {
    this.user = user;
    this.keyword = keyword;
    this.searchedAt = LocalDateTime.now();
  }

  public void refreshSearchedAt() {
    this.searchedAt = LocalDateTime.now();
  }
}
