CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_steps (
    username VARCHAR(50) PRIMARY KEY,
    steps INT NOT NULL DEFAULT 0
);
