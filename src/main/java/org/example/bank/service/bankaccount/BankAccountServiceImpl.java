package org.example.bank.service.bankaccount;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.GetBankResponse;
import com.fdkost.jee.soap.SellerBankAccount;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    @Override
    public Optional<BankAccountEntity> findByBankAccountId(UUID bankAccountId) {
        return bankAccountRepository.findById(bankAccountId);
    }

    @Override
    public Optional<BankAccountEntity> findByUserId(UUID userId) {
        return bankAccountRepository.findByClientId(userId);
    }

    @Override
    public Optional<BankAccountEntity> create(BankAccountEntity bankAccountEntity) {
        return Optional.of(bankAccountRepository.save(bankAccountEntity));
    }

    @Override
    public Optional<BankAccountEntity> update(BankAccountEntity bankAccountEntity) {
        return bankAccountRepository.findById(bankAccountEntity.getId())
                .map(bankAccountRepository::saveAndFlush);
    }

    @Override
    public void deleteById(BankAccountEntity bankAccountEntity) {
            bankAccountRepository.findById(bankAccountEntity.getId())
                    .map(entity -> {
                        bankAccountRepository.delete(entity);
                        bankAccountRepository.flush();
                        return true;
                    })
                    .orElse(false);
    }

    public BankAccountEntity checkBankAccountExists(ClientEntity clientEntity,BigDecimal sum) {
        Optional<BankAccountEntity> bankAccountEntityOptional = bankAccountRepository.findByClientId(clientEntity.getId());
        BankAccountEntity bankAccountEntity = null;
        if (bankAccountEntityOptional.isPresent()) {
            bankAccountEntity = bankAccountEntityOptional.get();
        }else {
            bankAccountEntityOptional = create(BankAccountEntity.builder()
                    .client(clientEntity)
                    .number("+3**********")
                    .sum(sum)
                    .id(UUID.randomUUID())
                    .build());
            if (bankAccountEntityOptional.isPresent()) {
                bankAccountEntity = bankAccountEntityOptional.get();
            }
        }
        return bankAccountEntity;
    }

    @Override
    public GetBankResponse fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, BigDecimal sum, GetBankResponse response){

        BankAccountEntity actualBuyerBankAccount = checkBankAccountExists(buyerBankAccount.getClient(),buyerBankAccount.getSum());
        BankAccountEntity actualSellerBankAccount = checkBankAccountExists(sellerBankAccount.getClient(),sellerBankAccount.getSum());

        if (actualBuyerBankAccount.getId()!=null && actualSellerBankAccount.getId() != null) {
            buyerBankAccount.setSum((buyerBankAccount.getSum().subtract(sum)));
            if (buyerBankAccount.getSum().doubleValue() < 0){
                buyerBankAccount.setSum((buyerBankAccount.getSum()).add(sum));
                update(buyerBankAccount);
                response.setStatus(500);
            }else {
                sellerBankAccount.setSum((sellerBankAccount.getSum().add(sum)));
                update(buyerBankAccount);
                update(sellerBankAccount);
                response.setStatus(200);
            }
        }
        return response;
    }

    @Override
    public BankAccountEntity convertBuyerBankAccount(BuyerBankAccount buyerFromXML){
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(UUID.fromString(buyerFromXML.getId()));
        entity.setNumber(buyerFromXML.getNumber());
        entity.setClient(ClientEntity.builder()
                .id(UUID.fromString(buyerFromXML.getClient().getId()))
                .name(buyerFromXML.getClient().getName())
                .openKey(buyerFromXML.getClient().getOpenKey())
                .build());
        entity.setSum(buyerFromXML.getSum());
        return entity;
    }

    @Override
    public BankAccountEntity convertSellerBankAccount(SellerBankAccount sellerBankAccount){
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(UUID.fromString(sellerBankAccount.getId()));
        entity.setNumber(sellerBankAccount.getNumber());
        entity.setClient(ClientEntity.builder()
                .id(UUID.fromString(sellerBankAccount.getClient().getId()))
                .name(sellerBankAccount.getClient().getName())
                .openKey(sellerBankAccount.getClient().getOpenKey())
                .build());
        entity.setSum(sellerBankAccount.getSum());
        return entity;
    }

    public byte[] encrypt(BigDecimal sum, BankAccountEntity bankAccountEntity) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        InputStream inputStream = getClass().getResourceAsStream("/privatekey.pem");
        String privateKeyContent = new String(inputStream.readAllBytes());

        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        privateKeyContent = privateKeyContent.replaceAll("\\s", "");
        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        PrivateKey privateKey = kf.generatePrivate(keySpecPKCS8);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,privateKey);
        String message = sum +"/"+bankAccountEntity.getId();
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(messageBytes);
    }

    @Override
    public byte[] decrypt(byte[] message,BigDecimal sum, BankAccountEntity bankAccountEntity, String publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory kf = KeyFactory.getInstance("RSA");

        publicKey = publicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        publicKey = publicKey.replaceAll("\\s", "");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }
}
