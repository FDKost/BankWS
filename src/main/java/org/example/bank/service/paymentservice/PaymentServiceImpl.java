package org.example.bank.service.paymentservice;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.model.PaymentModel;
import org.example.bank.model.PaymentOperationResult;
import org.example.bank.service.bankaccount.BankAccountService;
import org.example.bank.service.client.ClientService;
import org.example.bank.service.operation.OperationService;
import org.example.bank.service.signature.SignatureService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ClientService clientService;
    private final SignatureService signatureService;
    private final BankAccountService bankAccountService;
    private final OperationService operationService;

    @SneakyThrows
    @Override
    public PaymentOperationResult performPayment(PaymentModel request) {
        Optional<ClientEntity> existingSeller = clientService.checkClientExists(request.
                getSellerBankAccount().getClient().getName());
        Optional<ClientEntity> existingBuyer = clientService.checkClientExists(request.
                getBuyerBankAccount().getClient().getName());
        BankAccountEntity sellerBankAccount = bankAccountService.
                checkBankAccountExists(existingSeller.get(), BigDecimal.valueOf(0));
        BankAccountEntity buyerBankAccount = bankAccountService.
                checkBankAccountExists(existingBuyer.get(), BigDecimal.valueOf(0));

        if (signatureService.checkMessages(buyerBankAccount, sellerBankAccount, request)) {
            return operationService.fillGetBank(buyerBankAccount, sellerBankAccount, request.getSum(), request);
        } else return null;
    }
}
