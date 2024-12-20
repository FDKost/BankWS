package org.example.bank.service.bankaccount;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.GetBankRequest;
import com.fdkost.jee.soap.GetBankResponse;
import com.fdkost.jee.soap.SellerBankAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        return Optional.of(bankAccountRepository.saveAndFlush(bankAccountEntity));
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
    @Transactional
    public void fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount,
                            BigDecimal sum, GetBankResponse response){

        if (buyerBankAccount.getId()!=null && sellerBankAccount.getId() != null) {
            if (buyerBankAccount.getSum().doubleValue() > 0
                    && (buyerBankAccount.getSum().doubleValue() - sum.doubleValue()) >= 0){
                sellerBankAccount.setSum((sellerBankAccount.getSum().add(sum)));
                buyerBankAccount.setSum((buyerBankAccount.getSum().subtract(sum)));

                update(buyerBankAccount);
                update(sellerBankAccount);
                response.setStatus(200);
            }else {
                response.setStatus(500);
            }
        }
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

    private String convertPublicKey(String publicKey){
        publicKey = publicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----","").replace("-----END PUBLIC KEY-----","");
        publicKey = publicKey.replaceAll("\\s", "");
        return publicKey;
    }

    @Override
    public byte[] decrypt(byte[] message,BigDecimal sum, BankAccountEntity bankAccountEntity, String publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory kf = KeyFactory.getInstance("RSA");

        publicKey = convertPublicKey(publicKey);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));

        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }

    @Override
    public boolean checkMessages(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount, GetBankRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] bytesBuyerMessage = Base64.getDecoder().decode(request.getBuyerSecret());
        byte[] bytesSellerMessage = Base64.getDecoder().decode(request.getSellerSecret());
        byte[] decryptedBuyerMessage = decrypt(bytesBuyerMessage,request.getSum(),buyerBankAccount,buyerBankAccount.getClient().getOpenKey());
        byte[] decryptedSellerMessage = decrypt(bytesSellerMessage,request.getSum(),sellerBankAccount,sellerBankAccount.getClient().getOpenKey());
        String buyerMessage = new String(decryptedBuyerMessage, StandardCharsets.UTF_8);
        String sellerMessage = new String(decryptedSellerMessage, StandardCharsets.UTF_8);
        String checkBMessage = request.getSum()+"/"+buyerBankAccount.getClient().getName();
        String checkSMessage = request.getSum()+"/"+sellerBankAccount.getClient().getName();
        return buyerMessage.equals(checkBMessage) && sellerMessage.equals(checkSMessage);
    }
}
