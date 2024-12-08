package org.example.bank.endpoint;

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
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Optional;
import java.util.UUID;

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
        Optional<ClientEntity> existingBuyer = clientService.findById(UUID.fromString(request.getBuyerId()));
        if (existingBuyer.isEmpty()) {
            existingBuyer = Optional.ofNullable(clientService.create(ClientEntity.builder()
                    .name(request.getBuyerBankAccount().getClient().getName())
                    .id(UUID.fromString(request.getBuyerId()))
                    .build()));
        }
        Optional<ClientEntity> existingSeller = clientService.findByName(request.getSellerBankAccount().getClient().getName());
        GetBankResponse response = new GetBankResponse();
        if (existingBuyer.isPresent() && existingSeller.isPresent()) {
            BankAccountEntity sellerBankAccount = bankAccountService.checkBankAccountExists(existingSeller.get(), request.getSellerBankAccount().getSum());
            BankAccountEntity buyerBankAccount = bankAccountService.checkBankAccountExists(existingBuyer.get(), request.getBuyerBankAccount().getSum());

            if (bankAccountService.checkMessages(buyerBankAccount, sellerBankAccount, request)) {
                bankAccountService.fillGetBank(buyerBankAccount, sellerBankAccount, request.getSum(), response);
                checkCondition(response, buyerBankAccount, sellerBankAccount, request);
            }
        } else {
            response.setStatus(500);
            response.setMessage("Try again later");
            response.setTransactionId(null);
        }

        return response;
    }

    private GetBankResponse checkCondition(GetBankResponse response, BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, GetBankRequest request) {
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

        return response;
    }
}

