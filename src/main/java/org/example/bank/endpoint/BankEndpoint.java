package org.example.bank.endpoint;

import com.fdkost.jee.soap.GetBankRequest;
import com.fdkost.jee.soap.GetBankResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.entity.OperationEntity;
import org.example.bank.service.bankaccount.BankAccountService;
import org.example.bank.service.client.ClientService;
import org.example.bank.service.operation.OperationService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Endpoint
public class BankEndpoint {

    private final BankAccountService bankAccountService;
    private final ClientService clientService;
    private final OperationService operationService;

    @SneakyThrows
    @PayloadRoot(namespace = "http://soap.jee.fdkost.com/", localPart = "getBankRequest")
    @ResponsePayload
    public GetBankResponse getBank(@RequestPayload GetBankRequest request) {
        GetBankResponse response = new GetBankResponse();
        try {
            Optional<ClientEntity> existingSeller = clientService.findByName(request.
                    getSellerBankAccount().getClient().getName());
            Optional<ClientEntity> existingBuyer = clientService.findByName(request.
                    getBuyerBankAccount().getClient().getName());
            if (existingBuyer.isEmpty() && existingSeller.isPresent()) {
                ClientEntity entity = new ClientEntity();
                entity.setId(UUID.fromString(request.getBuyerId()));
                entity.setName(request.getBuyerBankAccount().getClient().getName());
                entity.setOpenKey(existingSeller.get().getOpenKey());
                existingBuyer = Optional.ofNullable(clientService.create(entity));
            }
            if (existingBuyer.isPresent() && existingSeller.isPresent()) {
                BankAccountEntity sellerBankAccount = bankAccountService.
                        checkBankAccountExists(existingSeller.get(), BigDecimal.valueOf(1000));
                BankAccountEntity buyerBankAccount = bankAccountService.
                        checkBankAccountExists(existingBuyer.get(), request.getBuyerBankAccount().getSum());
                buyerBankAccount.setSum(request.getSum());
                bankAccountService.update(buyerBankAccount);

                if (bankAccountService.checkMessages(buyerBankAccount, sellerBankAccount, request)) {
                    bankAccountService.fillGetBank(buyerBankAccount, sellerBankAccount, request.getSum(), response);
                    checkCondition(response, buyerBankAccount, sellerBankAccount, request);
                }
            } else {
                response.setStatus(500);
                response.setMessage("Try again later");
                response.setTransactionId(null);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void checkCondition(GetBankResponse response, BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, GetBankRequest request) {
        if (response.getStatus() == 200) {
            response.setMessage("Transaction complete");
            OperationEntity operation = operationService.create(OperationEntity.builder()
                    .sum(request.getSum())
                    .id(UUID.randomUUID())
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
}

