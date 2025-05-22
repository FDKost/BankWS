--liquibase formatted sql

--changeset FDKost:1
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--changeset FDKost:2
CREATE TABLE IF NOT EXISTS client
(
    id       uuid PRIMARY KEY,
    name     TEXT,
    open_key TEXT DEFAULT '-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqoGRIo5jDNpAsn/oPb+j
4IeiAta6vyFRNAFDufNi0+vH3w2mZbFMzjrgM49bc3rBOglo/zyRAHieIELmp8Cu
KJ9gzhrHgf2+oJmdEtwxEEuQqbJJ3PV6tYoBHgrjGAHVCOUndjYSojMIPTM3gv2F
a3AYWaat+BXIbS2Ax5LMK9LRUFqoZJyMvpMdOL2RWSim/XBIosyYITy2Azv1zm8Q
HjHmbUkhgyHQr3oJxiLfSNXaKdad2nKsRKVUNwWgalythusuDkrMBuL8hgWRdm7T
gHn3MjkLouypyuTXI37iCsLpVPdqjaYy//13XDNZghcvT0jSqEKMnNzO4FSvzEh7
FwIDAQAB
-----END PUBLIC KEY-----'
);
CREATE TABLE IF NOT EXISTS bank_account
(
    id      uuid PRIMARY KEY,
    user_id uuid,
    number  TEXT,
    sum     DECIMAL
);
CREATE TABLE IF NOT EXISTS operation
(
    id                     uuid PRIMARY KEY,
    recipient_bank_account uuid,
    sender_bank_account    uuid,
    transaction            uuid,
    sum                    DECIMAL
)