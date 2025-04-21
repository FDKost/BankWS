package org.example.bank.service.operation;

import lombok.RequiredArgsConstructor;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.OperationEntity;
import org.example.bank.model.PaymentModel;
import org.example.bank.model.PaymentOperationResult;
import org.example.bank.repository.OperationRepository;
import org.example.bank.service.bankaccount.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationServiceImpl implements OperationService {
    private final OperationRepository operationRepository;
    private final BankAccountService bankAccountService;

    @Override
    public Optional<OperationEntity> findById(UUID id) {
        return operationRepository.findById(id);
    }

    @Override
    public Optional<OperationEntity> findByTransaction(UUID transactionId) {
        return operationRepository.findByTransaction(transactionId);
    }

    @Override
    public OperationEntity create(OperationEntity operation) {
        return operationRepository.save(operation);
    }

    @Override
    public Optional<OperationEntity> update(OperationEntity operation) {
        return operationRepository.findById(operation.getId())
                .map(operationRepository::saveAndFlush);
    }

    @Override
    public void deleteById(OperationEntity operation) {
        operationRepository.findById(operation.getId())
                .map(entity -> {
                    operationRepository.delete(entity);
                    operationRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    @Override
    public PaymentOperationResult fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount,
                                              BigDecimal sum, PaymentModel request) {
        PaymentOperationResult result = new PaymentOperationResult();
        if (buyerBankAccount.getId() != null && sellerBankAccount.getId() != null) {
            if (buyerBankAccount.getSum().doubleValue() > 0
                    && (buyerBankAccount.getSum().doubleValue() - sum.doubleValue()) >= 0) {
                sellerBankAccount.setSum((sellerBankAccount.getSum().add(sum)));
                buyerBankAccount.setSum((buyerBankAccount.getSum().subtract(sum)));

                bankAccountService.update(buyerBankAccount);
                bankAccountService.update(sellerBankAccount);

                result = PaymentOperationResult.success(UUID.randomUUID().toString());
            } else {
                result = PaymentOperationResult.failure("Insufficient funds in the bank account,\" +\n" +
                        "                \" check your bank account,operation was not successful");
            }
        }
        return result;
    }
}
