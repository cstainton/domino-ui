-- PostgREST needs a schema to expose and two roles:
--   web_anon       – unauthenticated requests run as this role
--   authenticator  – PostgREST connects as this role and switches to web_anon

CREATE ROLE web_anon NOLOGIN;
CREATE ROLE authenticator NOINHERIT LOGIN PASSWORD 'authenticator_pass';
GRANT web_anon TO authenticator;

CREATE SCHEMA api;
GRANT USAGE ON SCHEMA api TO web_anon;

-- Todos table ---------------------------------------------------------------

CREATE TABLE api.todos (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     INTEGER      NOT NULL DEFAULT 1,
    title       TEXT         NOT NULL,
    completed   BOOLEAN      NOT NULL DEFAULT FALSE,
    lat         DOUBLE PRECISION,
    lng         DOUBLE PRECISION,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Allow anonymous reads and writes for the demo
GRANT SELECT, INSERT, UPDATE, DELETE ON api.todos TO web_anon;
GRANT USAGE, SELECT ON SEQUENCE api.todos_id_seq TO web_anon;

-- Seed a handful of sample todos so the list isn't empty on first load
INSERT INTO api.todos (user_id, title, completed) VALUES
    (1, 'Buy groceries',             false),
    (1, 'Walk the dog',              true),
    (1, 'Read a book',               false),
    (2, 'Fix the leaky tap',         false),
    (2, 'Call the dentist',          true),
    (1, 'Write unit tests',          false),
    (3, 'Deploy to production',      false),
    (3, 'Review pull requests',      true),
    (1, 'Plan weekend trip',         false),
    (2, 'Organise the garage',       false);
