package org.example.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "operation")
@Builder
@Entity
public class OperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_bank_account")
    BankAccountEntity recipientBankAccount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_bank_account")
    BankAccountEntity senderBankAccount;

    @Column(name = "transaction")
    UUID transaction;

    @Column(name = "sum")
    BigDecimal sum;

}
