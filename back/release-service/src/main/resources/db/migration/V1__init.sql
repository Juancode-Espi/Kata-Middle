-- Flyway migration V1: Initial schema for release requests

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS release_request (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha            TIMESTAMP       NOT NULL,
    equipo           VARCHAR(100)    NOT NULL,
    tipo             VARCHAR(10)     NOT NULL,
    descripcion      TEXT            NOT NULL,
    pr_id            VARCHAR(100),
    cobertura        NUMERIC(5, 2),
    stack            VARCHAR(300),
    estado           VARCHAR(30)     NOT NULL,
    tipo_aprobacion  VARCHAR(10)     NOT NULL,
    razones_rechazo  TEXT
);
