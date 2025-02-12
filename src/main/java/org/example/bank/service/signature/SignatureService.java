package org.example.bank.service.signature;

import com.fdkost.jee.soap.GetBankRequest;
import org.example.bank.entity.BankAccountEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface SignatureService {
    boolean checkMessages(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, GetBankRequest request)
            throws NoSuchPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    byte[] decrypt(byte[] message, BigDecimal sum, BankAccountEntity bankAccountEntity, String publicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException;
}
