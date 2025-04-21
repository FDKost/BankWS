package org.example.bank.service.response;

import com.fdkost.jee.soap.GetBankResponse;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.model.PaymentModel;

public interface ResponseService {

    GetBankResponse setTransactionComplete(GetBankResponse response, PaymentModel request,
                                           BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount);

    GetBankResponse setTransactionFailedNoMoney(GetBankResponse response);

    GetBankResponse setTransactionFailedNoUser(GetBankResponse response);
}
