package org.example.bank.service.paymentservice;

import org.example.bank.model.PaymentModel;
import org.example.bank.model.PaymentOperationResult;

public interface PaymentService {

    PaymentOperationResult performPayment(PaymentModel request);
}
