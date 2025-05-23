package org.example.bank.service.operation;

import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.OperationEntity;
import org.example.bank.model.PaymentModel;
import org.example.bank.model.PaymentOperationResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface OperationService {
    Optional<OperationEntity> findById(UUID id);

    Optional<OperationEntity> findByTransaction(UUID transactionId);

    OperationEntity create(OperationEntity operation);

    Optional<OperationEntity> update(OperationEntity operation);

    void deleteById(OperationEntity operation);

    PaymentOperationResult fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, BigDecimal sum,
                                       PaymentModel request);
}
