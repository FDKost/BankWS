CREATE TABLE IF NOT EXISTS client
(
    id       uuid PRIMARY KEY,
    name     TEXT,
    open_key TEXT
);
CREATE TABLE IF NOT EXISTS bank_account
(
    id     uuid PRIMARY KEY,
    userId uuid,
    number TEXT,
    sum    DECIMAL
);
CREATE TABLE IF NOT EXISTS operation
(
    id                     uuid PRIMARY KEY,
    recipient_bank_account uuid,
    sender_bank_account    uuid,
    transaction            uuid,
    sum                    DECIMAL
)