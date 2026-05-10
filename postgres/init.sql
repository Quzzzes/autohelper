-- Схема базы данных «Авто-помощник BY»
-- Выполняется автоматически при первом запуске контейнера PostgreSQL

-- Расширение для UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ─── Пользователи ─────────────────────────────────────────────
CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  is_premium    BOOLEAN DEFAULT FALSE,
  premium_until TIMESTAMPTZ,
  created_at    TIMESTAMPTZ DEFAULT NOW()
);

-- ─── Автомобили ───────────────────────────────────────────────
CREATE TABLE cars (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  plate      VARCHAR(10) NOT NULL,    -- госномер РБ: АААNNNN или A NNNN AA
  make       VARCHAR(60),             -- марка
  model      VARCHAR(60),             -- модель
  year       SMALLINT,               -- год выпуска
  vin        VARCHAR(17),            -- VIN (опционально)
  photo_url  TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_cars_user_id ON cars(user_id);

-- ─── Напоминания ──────────────────────────────────────────────
CREATE TABLE reminders (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  car_id       UUID NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
  type         VARCHAR(30) NOT NULL,  -- osgo|to|techosmotr|blue_card|oil|tires|vu
  due_date     DATE NOT NULL,
  notified_30  BOOLEAN DEFAULT FALSE,
  notified_14  BOOLEAN DEFAULT FALSE,
  notified_3   BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_reminders_car_id  ON reminders(car_id);
CREATE INDEX idx_reminders_due_date ON reminders(due_date);

-- ─── Штрафы ───────────────────────────────────────────────────
CREATE TABLE fines (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  car_id          UUID NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
  resolution_no   VARCHAR(30) UNIQUE,   -- номер постановления МВД
  article         VARCHAR(30),          -- статья КоАП
  amount          DECIMAL(10,2),        -- сумма в BYN
  fine_date       DATE,
  status          VARCHAR(10) DEFAULT 'unpaid',  -- unpaid | paid | overdue
  erip_invoice_no INTEGER,             -- номер счёта в Express-pay.by
  paid_at         TIMESTAMPTZ
);

CREATE INDEX idx_fines_car_id ON fines(car_id);
CREATE INDEX idx_fines_status ON fines(status);

-- ─── Расходы ──────────────────────────────────────────────────
CREATE TABLE expenses (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  car_id       UUID NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
  category     VARCHAR(20) NOT NULL,  -- fuel|repair|insurance|wash|parking|fine|techosmotr|other
  amount       DECIMAL(10,2) NOT NULL,
  liters       DECIMAL(6,2),          -- для топлива: количество литров
  odometer     INT,                   -- показания одометра
  note         TEXT,
  expense_date DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_expenses_car_id       ON expenses(car_id);
CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);

-- ─── СТО ──────────────────────────────────────────────────────
CREATE TABLE sto (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name           VARCHAR(120) NOT NULL,
  city           VARCHAR(60),
  address        TEXT,
  phone          VARCHAR(20),
  lat            DECIMAL(9,6),
  lng            DECIMAL(9,6),
  services       TEXT[],              -- ['oil','tires','body','engine','to']
  is_premium     BOOLEAN DEFAULT FALSE,
  rating         DECIMAL(3,2) DEFAULT 0,
  reviews_count  INT DEFAULT 0
);

CREATE INDEX idx_sto_city ON sto(city);

-- ─── Отзывы на СТО ────────────────────────────────────────────
CREATE TABLE sto_reviews (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  sto_id     UUID NOT NULL REFERENCES sto(id) ON DELETE CASCADE,
  rating     SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment    TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, sto_id)             -- один пользователь = один отзыв на СТО
);

-- ─── Тестовые данные для локальной разработки ─────────────────
INSERT INTO sto (name, city, address, phone, services, is_premium, rating) VALUES
  ('СТО Автосервис Минск', 'Минск', 'ул. Тимирязева, 65', '+375 17 234-56-78', ARRAY['oil','tires','to'], true, 4.8),
  ('Шиномонтаж Экспресс',  'Минск', 'пр. Независимости, 120', '+375 29 111-22-33', ARRAY['tires'], false, 4.2),
  ('СТО Автомастер',       'Минск', 'ул. Кальварийская, 17', '+375 44 555-66-77', ARRAY['body','engine','oil'], false, 4.5);
