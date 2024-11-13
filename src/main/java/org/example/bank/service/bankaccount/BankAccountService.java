package org.example.bank.service.bankaccount;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.GetBankResponse;
import com.fdkost.jee.soap.SellerBankAccount;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountService {
    Optional<BankAccountEntity> findByBankAccountId(UUID bankAccountId);
    Optional<BankAccountEntity> findByUserId(UUID userId);
    Optional<BankAccountEntity> create(BankAccountEntity bankAccountEntity);
    Optional<BankAccountEntity> update(BankAccountEntity bankAccountEntity);
    BankAccountEntity convertBuyerBankAccount(BuyerBankAccount buyerFromXML);
    BankAccountEntity convertSellerBankAccount(SellerBankAccount sellerBankAccount);
    GetBankResponse fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, BigDecimal sum, GetBankResponse response);
    byte[] decrypt(byte[] message,BigDecimal sum, BankAccountEntity bankAccountEntity, String publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException;
    void deleteById(BankAccountEntity bankAccountEntity);
    BankAccountEntity checkBankAccountExists(ClientEntity clientEntity,BigDecimal sum);
    byte[] encrypt(BigDecimal sum, BankAccountEntity bankAccountEntity) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
}
