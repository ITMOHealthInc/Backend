CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL
);


CREATE TABLE IF NOT EXISTS user_steps (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    steps INT NOT NULL DEFAULT 0,
    goal INT NOT NULL DEFAULT 10000,
    date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS user_measurements (
    username         VARCHAR(50)      PRIMARY KEY,
    weight           NUMERIC(8,2)     NULL,         -- Вес (кг)
    waist            NUMERIC(8,2)     NULL,         -- Талия (см)
    hips             NUMERIC(8,2)     NULL,         -- Бёдра (см)
    chest            NUMERIC(8,2)     NULL,         -- Грудь (см)
    arms             NUMERIC(8,2)     NULL,         -- Руки (см)
    body_fat         NUMERIC(5,2)     NULL,         -- Объём жира (%)
    muscle_mass      NUMERIC(8,2)     NULL,         -- Мышечная масса (кг)
    blood_glucose    NUMERIC(5,2)     NULL,         -- Глюкоза в крови (ммоль/л)
    bp_systolic      INTEGER          NULL,         -- Давление систолическое (мм рт.ст.)
    bp_diastolic     INTEGER          NULL,         -- Давление диастолическое (мм рт.ст.)
    measured_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()  -- дата/время замера
);