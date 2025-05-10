package org.example.bank.endpoint;

import com.fdkost.jee.soap.GetBankRequest;
import com.fdkost.jee.soap.GetBankResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.model.PaymentModel;
import org.example.bank.model.PaymentOperationResult;
import org.example.bank.service.client.ClientService;
import org.example.bank.service.paymentservice.PaymentService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@RequiredArgsConstructor
@Endpoint
public class BankEndpoint {
    private final ClientService clientService;
    private final PaymentService paymentService;

    @SneakyThrows
    @PayloadRoot(namespace = "http://soap.jee.fdkost.com/", localPart = "getBankRequest")
    @ResponsePayload
    public GetBankResponse payment(@RequestPayload GetBankRequest request) {
        PaymentModel paymentModel = PaymentModel.fromRequest(request);
        PaymentOperationResult operationResult = new PaymentOperationResult();
        try {
            if (Boolean.TRUE.equals(clientService.checkClientsExists(paymentModel
                            .getBuyerBankAccount()
                            .getClient().getName(),
                    paymentModel
                            .getSellerBankAccount()
                            .getClient().getName()))) {
                operationResult = paymentService.performPayment(paymentModel);
            } else {
                operationResult = PaymentOperationResult.failure("No such user in our bank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PaymentOperationResult.fromResult(operationResult);
    }
}

