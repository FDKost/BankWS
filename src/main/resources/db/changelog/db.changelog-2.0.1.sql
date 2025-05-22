ALTER TABLE bank_account
    ADD
        FOREIGN KEY (userId) REFERENCES client (id);

ALTER TABLE operation
    ADD
        FOREIGN KEY (recipient_bank_account) REFERENCES bank_account (id);

ALTER TABLE operation
    ADD
        FOREIGN KEY (sender_bank_account) REFERENCES bank_account (id);