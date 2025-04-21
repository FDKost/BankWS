package org.example.bank.model;

import com.fdkost.jee.soap.GetBankResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOperationResult {
    private int status;
    private String message;
    private String transactionId;

    public static PaymentOperationResult success(String transactionId) {
        return new PaymentOperationResult(200, "Payment successful", transactionId);
    }

    public static PaymentOperationResult failure(String message) {
        return new PaymentOperationResult(500, message, null);
    }

    public static GetBankResponse fromResult(PaymentOperationResult result) {
        GetBankResponse response = new GetBankResponse();
        response.setStatus(result.getStatus());
        response.setMessage(result.getMessage());
        response.setTransactionId(result.getTransactionId());
        return response;
    }
}
