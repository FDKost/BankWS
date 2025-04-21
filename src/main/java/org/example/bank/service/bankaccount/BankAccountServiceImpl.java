package org.example.bank.service.bankaccount;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.GetBankResponse;
import com.fdkost.jee.soap.SellerBankAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public BankAccountEntity create(BankAccountEntity bankAccountEntity) {
        return bankAccountRepository.save(bankAccountEntity);
    }

    @Override
    @Transactional
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

    public BankAccountEntity checkBankAccountExists(ClientEntity clientEntity, BigDecimal sum) {
        Optional<BankAccountEntity> bankAccountEntityOptional = bankAccountRepository.findByClientId(clientEntity.getId());
        BankAccountEntity bankAccountEntity;
        if (bankAccountEntityOptional.isPresent()) {
            bankAccountEntity = bankAccountEntityOptional.get();
        } else {
            BankAccountEntity entityToSave = new BankAccountEntity();
            entityToSave.setClient(clientEntity);
            entityToSave.setNumber("+3**********");
            entityToSave.setSum(sum);

            bankAccountEntity = create(entityToSave);
        }
        return bankAccountEntity;
    }

    @Override
    @Transactional
    public void fillGetBank(BankAccountEntity buyerBankAccount, BankAccountEntity sellerBankAccount,
                            BigDecimal sum, GetBankResponse response) {

        if (buyerBankAccount.getId() != null && sellerBankAccount.getId() != null) {
            if (buyerBankAccount.getSum().doubleValue() > 0
                    && (buyerBankAccount.getSum().doubleValue() - sum.doubleValue()) >= 0) {
                sellerBankAccount.setSum((sellerBankAccount.getSum().add(sum)));
                buyerBankAccount.setSum((buyerBankAccount.getSum().subtract(sum)));

                update(buyerBankAccount);
                update(sellerBankAccount);
                response.setStatus(200);
            } else {
                response.setStatus(500);
            }
        }
    }

    @Override
    public BankAccountEntity convertBuyerBankAccount(BuyerBankAccount buyerFromXML) {
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
    public BankAccountEntity convertSellerBankAccount(SellerBankAccount sellerBankAccount) {
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
}
