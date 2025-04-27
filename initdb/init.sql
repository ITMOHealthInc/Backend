CREATE TABLE IF NOT EXISTS users (
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

CREATE TABLE IF NOT EXISTS products(
      id SERIAL PRIMARY KEY,
      name TEXT NOT NULL,
      affiliation TEXT NOT NULL,
      water DOUBLE PRECISION NOT NULL,
      mass DOUBLE PRECISION NOT NULL,

      fiber DOUBLE PRECISION NOT NULL,
      sugar DOUBLE PRECISION NOT NULL,
      added_sugar DOUBLE PRECISION,

      saturated_fat DOUBLE PRECISION NOT NULL,
      polyunsaturated_fat DOUBLE PRECISION,

      cholesterol DOUBLE PRECISION,
      salt DOUBLE PRECISION NOT NULL,
      alcohol DOUBLE PRECISION,

      vitamin_b7 DOUBLE PRECISION,
      vitamin_c DOUBLE PRECISION,
      vitamin_d DOUBLE PRECISION,
      vitamin_e DOUBLE PRECISION,
      vitamin_k DOUBLE PRECISION,

      calcium DOUBLE PRECISION,
      iron DOUBLE PRECISION,
      zinc DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS user_products(
    username VARCHAR(256),
    product_id INTEGER
);

CREATE TABLE IF NOT EXISTS recipe(
    id INTEGER,
    name VARCHAR(256),
    username VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS recipe_products(
    recipe_id INTEGER,
    product_id INTEGER
);