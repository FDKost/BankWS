package org.example.bank.service.paymentservice;


import com.fdkost.jee.soap.GetBankRequest;
import com.fdkost.jee.soap.GetBankResponse;
import org.example.bank.entity.BankAccountEntity;

import java.math.BigDecimal;

public interface PaymentService {
    void fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, BigDecimal sum,
                     GetBankResponse response,GetBankRequest request);
    void performPayment(GetBankRequest request,GetBankResponse response);
}
