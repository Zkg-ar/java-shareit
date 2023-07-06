DROP TABLE IF EXISTS users, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);


CREATE TABLE IF NOT EXISTS requests(

    id BIGINT GENERATED  BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR(200) NOT NULL,
    requestor_id BIGINT,
    created_time datetime NOT NULL,
    FOREIGN KEY (requestor_id) REFERENCES requests (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS items(

    id BIGINT GENERATED  BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL ,
    description VARCHAR(200) NOT NULL,
    is_available boolean,
    owner_id BIGINT NOT NULL ,
    request_id BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS bookings(

    id BIGINT GENERATED  BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date datetime NOT NULL,
    end_date datetime NOT NULL,
    item_id BIGINT NOT NULL ,
    booker_id BIGINT NOT NULL ,
    state VARCHAR(50),
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id) REFERENCES requests (id) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text    VARCHAR(200)                                        NOT NULL,
    item_id BIGINT,
    author_id BIGINT,
    created_date datetime,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE

);
