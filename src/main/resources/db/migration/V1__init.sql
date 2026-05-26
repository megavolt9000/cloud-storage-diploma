CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(255)
);

CREATE TABLE files (

    id BIGSERIAL PRIMARY KEY,

    filename VARCHAR(255) NOT NULL,

    file_size BIGINT NOT NULL,

    path VARCHAR(500) NOT NULL,

    upload_date TIMESTAMP,

    user_id BIGINT NOT NULL,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);