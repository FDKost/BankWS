package org.example.bank.model;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.GetBankRequest;
import com.fdkost.jee.soap.SellerBankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel {
    private String sellerId;
    private String sellerSecret;
    private SellerBankAccount sellerBankAccount;
    private String buyerId;
    private String buyerSecret;
    private BuyerBankAccount buyerBankAccount;
    private BigDecimal sum;

    public static PaymentModel fromRequest(GetBankRequest request) {
        return new PaymentModel(
                request.getSellerId(),
                request.getSellerSecret(),
                request.getSellerBankAccount(),
                request.getBuyerId(),
                request.getBuyerSecret(),
                request.getBuyerBankAccount(),
                request.getSum()
        );
    }
}
