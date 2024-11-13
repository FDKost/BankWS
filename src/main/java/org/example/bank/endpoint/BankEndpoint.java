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

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Endpoint
public class BankEndpoint {

    private final BankAccountService bankAccountService;
    private final ClientService clientService;
    private final OperationService operationService;

    @SneakyThrows
    @PayloadRoot(namespace = "http://soap.jee.fdkost.com/",localPart = "getBankRequest")
    @ResponsePayload
    public GetBankResponse getBank(@RequestPayload GetBankRequest request){
        Optional<ClientEntity>existingBuyer=clientService.findById(UUID.fromString(request.getBuyerId()));
        Optional<ClientEntity>existingSeller=clientService.findById(UUID.fromString(request.getSellerId()));
        GetBankResponse response = new GetBankResponse();
        if(existingBuyer.isPresent() && existingSeller.isPresent()){
            BankAccountEntity sellerBankAccount = bankAccountService.checkBankAccountExists(existingSeller.get(),request.getSellerBankAccount().getSum());
            BankAccountEntity buyerBankAccount = bankAccountService.checkBankAccountExists(existingBuyer.get(),request.getBuyerBankAccount().getSum());

            byte[] bytesBuyerMessage = bankAccountService.encrypt(request.getSum(),buyerBankAccount);
            byte[] bytesSellerMessage = bankAccountService.encrypt(request.getSum(),sellerBankAccount);
            byte[] decryptedBuyerMessage = bankAccountService.decrypt(bytesBuyerMessage,request.getSum(),buyerBankAccount,buyerBankAccount.getClient().getOpenKey());
            byte[] decryptedSellerMessage = bankAccountService.decrypt(bytesSellerMessage,request.getSum(),sellerBankAccount,sellerBankAccount.getClient().getOpenKey());
            String buyerMessage = new String(decryptedBuyerMessage, StandardCharsets.UTF_8);
            String sellerMessage = new String(decryptedSellerMessage, StandardCharsets.UTF_8);
            String checkBMessage = request.getSum()+"/"+buyerBankAccount.getId();
            String checkSMessage = request.getSum()+"/"+sellerBankAccount.getId();
            if(buyerMessage.equals(checkBMessage)&&sellerMessage.equals(checkSMessage)){
                //реализация
                bankAccountService.fillGetBank(buyerBankAccount,sellerBankAccount,request.getSum(),response);

                if(response.getStatus()==200){
                    response.setMessage("Transaction complete");
                    OperationEntity operation = operationService.create(OperationEntity.builder()
                            .sum(request.getSum())
                            .id(UUID.randomUUID())
                            .recipientBankAccount(buyerBankAccount)
                            .senderBankAccount(sellerBankAccount)
                            .transaction(UUID.randomUUID())
                            .build());
                    response.setTransactionId(String.valueOf(operation.getTransaction()));
                } else if (response.getStatus()==500) {
                    response.setMessage("Insufficient funds in the bank account," +
                            " check your bank account,operation was not successful");
                    response.setTransactionId(null);
                } else {
                    response.setMessage("Something went wrong");
                    response.setTransactionId(null);
                }
            }
        }else {
            response.setStatus(500);
            response.setMessage("User not found");
            response.setTransactionId(null);
        }

        return response;
    }
}
