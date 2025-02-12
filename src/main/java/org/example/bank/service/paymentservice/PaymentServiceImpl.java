package org.example.bank.service.paymentservice;

import com.fdkost.jee.soap.GetBankRequest;
import com.fdkost.jee.soap.GetBankResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.entity.OperationEntity;
import org.example.bank.service.bankaccount.BankAccountService;
import org.example.bank.service.client.ClientService;
import org.example.bank.service.operation.OperationService;
import org.example.bank.service.signature.SignatureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ClientService clientService;
    private final SignatureService signatureService;
    private final OperationService operationService;
    private final BankAccountService bankAccountService;


    @Transactional
    @Override
    public void fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount,
                            BigDecimal sum, GetBankResponse response, GetBankRequest request){

        if (buyerBankAccount.getId()!=null && sellerBankAccount.getId() != null) {
            if (buyerBankAccount.getSum().doubleValue() > 0
                    && (buyerBankAccount.getSum().doubleValue() - sum.doubleValue()) >= 0){
                sellerBankAccount.setSum((sellerBankAccount.getSum().add(sum)));
                buyerBankAccount.setSum((buyerBankAccount.getSum().subtract(sum)));

                bankAccountService.update(buyerBankAccount);
                bankAccountService.update(sellerBankAccount);
                response.setStatus(200);
            }else {
                response.setStatus(500);
            }
        }

        if (response.getStatus() == 200) {
            response.setMessage("Transaction complete");
            OperationEntity operation = operationService.create(OperationEntity.builder()
                    .sum(request.getSum())
                    .recipientBankAccount(buyerBankAccount)
                    .senderBankAccount(sellerBankAccount)
                    .transaction(UUID.randomUUID())
                    .build());
            response.setTransactionId(String.valueOf(operation.getTransaction()));
        } else if (response.getStatus() == 500) {
            response.setMessage("Insufficient funds in the bank account," +
                    " check your bank account,operation was not successful");
            response.setTransactionId(null);
        } else {
            response.setMessage("Something went wrong");
            response.setTransactionId(null);
        }
    }

    @SneakyThrows
    @Override
    public void performPayment(GetBankRequest request, GetBankResponse response) {
        Optional<ClientEntity> existingSeller = clientService.checkClientExists(request.
                getSellerBankAccount().getClient().getName());
        Optional<ClientEntity> existingBuyer = clientService.checkClientExists(request.
                getBuyerBankAccount().getClient().getName());
        BankAccountEntity sellerBankAccount = bankAccountService.
                checkBankAccountExists(existingSeller.get(), BigDecimal.valueOf(0));
        BankAccountEntity buyerBankAccount = bankAccountService.
                checkBankAccountExists(existingBuyer.get(), BigDecimal.valueOf(0));

        if (signatureService.checkMessages(buyerBankAccount, sellerBankAccount, request)) {
            fillGetBank(buyerBankAccount, sellerBankAccount, request.getSum(), response,request);
        }
    }
}
