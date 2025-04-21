package org.example.bank.service.response;

import com.fdkost.jee.soap.GetBankResponse;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.OperationEntity;
import org.example.bank.model.PaymentModel;
import org.example.bank.service.operation.OperationService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private static OperationService operationService;

    @Override
    public GetBankResponse setTransactionComplete(GetBankResponse response, PaymentModel request, BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount) {
        response.setMessage("Transaction complete");
        OperationEntity operation = operationService.create(OperationEntity.builder()
                .sum(request.getSum())
                .recipientBankAccount(buyerBankAccount)
                .senderBankAccount(sellerBankAccount)
                .transaction(UUID.randomUUID())
                .build());
        response.setTransactionId(String.valueOf(operation.getTransaction()));

        return response;
    }

    @Override
    public GetBankResponse setTransactionFailedNoMoney(GetBankResponse response) {
        response.setMessage("Insufficient funds in the bank account," +
                " check your bank account,operation was not successful");
        response.setTransactionId(null);
        return response;
    }

    @Override
    public GetBankResponse setTransactionFailedNoUser(GetBankResponse response) {
        response.setStatus(500);
        response.setMessage("No such user in our bank");
        response.setTransactionId(null);
        return response;
    }

}
