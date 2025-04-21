package org.example.bank.service.bankaccount;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.GetBankResponse;
import com.fdkost.jee.soap.SellerBankAccount;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountService {
    Optional<BankAccountEntity> findByBankAccountId(UUID bankAccountId);

    Optional<BankAccountEntity> findByUserId(UUID userId);

    BankAccountEntity create(BankAccountEntity bankAccountEntity);

    Optional<BankAccountEntity> update(BankAccountEntity bankAccountEntity);

    BankAccountEntity convertBuyerBankAccount(BuyerBankAccount buyerFromXML);

    BankAccountEntity convertSellerBankAccount(SellerBankAccount sellerBankAccount);

    void fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, BigDecimal sum, GetBankResponse response);

    void deleteById(BankAccountEntity bankAccountEntity);

    BankAccountEntity checkBankAccountExists(ClientEntity clientEntity, BigDecimal sum);
}
