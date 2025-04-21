package org.example.bank.service.signature;

import org.example.bank.entity.BankAccountEntity;
import org.example.bank.model.PaymentModel;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SignatureServiceImpl implements SignatureService {
    @Override
    public boolean checkMessages(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, PaymentModel request) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] bytesBuyerMessage = Base64.getDecoder().decode(request.getBuyerSecret());
        byte[] bytesSellerMessage = Base64.getDecoder().decode(request.getSellerSecret());
        byte[] decryptedBuyerMessage = decrypt(bytesBuyerMessage, request.getSum(), buyerBankAccount, buyerBankAccount.getClient().getOpenKey());
        byte[] decryptedSellerMessage = decrypt(bytesSellerMessage, request.getSum(), sellerBankAccount, sellerBankAccount.getClient().getOpenKey());
        String buyerMessage = new String(decryptedBuyerMessage, StandardCharsets.UTF_8);
        String sellerMessage = new String(decryptedSellerMessage, StandardCharsets.UTF_8);
        String checkBMessage = request.getSum() + "/" + buyerBankAccount.getClient().getName();
        String checkSMessage = request.getSum() + "/" + sellerBankAccount.getClient().getName();
        return buyerMessage.equals(checkBMessage) && sellerMessage.equals(checkSMessage);
    }

    private String convertPublicKey(String publicKey) {
        publicKey = publicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        publicKey = publicKey.replaceAll("\\s", "");
        return publicKey;
    }

    @Override
    public byte[] decrypt(byte[] message, BigDecimal sum, BankAccountEntity bankAccountEntity, String publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory kf = KeyFactory.getInstance("RSA");

        publicKey = convertPublicKey(publicKey);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));

        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }
}
