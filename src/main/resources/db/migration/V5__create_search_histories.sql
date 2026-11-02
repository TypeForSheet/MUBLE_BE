
CREATE TABLE search_histories (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                keyword VARCHAR(255) NOT NULL,
                                searched_at DATETIME(6) NOT NULL,

                                PRIMARY KEY (id),

                                CONSTRAINT uk_search_histories_user_keyword
                                  UNIQUE (user_id, keyword),

                                CONSTRAINT fk_search_histories_user
                                  FOREIGN KEY (user_id) REFERENCES users(id),

                                INDEX idx_search_histories_user_searched_at
                                  (user_id, searched_at DESC, id DESC)
);
