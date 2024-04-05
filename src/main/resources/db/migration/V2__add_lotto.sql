CREATE TABLE lotto
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY,
    user_id      BIGINT,
    round        INTEGER      NOT NULL,
    created_date TIMESTAMP(6) NOT NULL,
    numbers      VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE INDEX user_id_index ON lotto (user_id);

ALTER TABLE lotto
    ADD CONSTRAINT FK_LOTTO_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
