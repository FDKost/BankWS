package org.example.bank.service.client;

import lombok.RequiredArgsConstructor;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.repository.ClientRepository;
import org.example.bank.service.bankaccount.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final BankAccountService bankAccountService;

    @Override
    public Optional<ClientEntity> findById(UUID uuid) {
        return clientRepository.findById(uuid);
    }

    @Override
    public Optional<ClientEntity> findByName(String name) {
        return clientRepository.findByName(name);
    }

    @Override
    public List<ClientEntity> findAll() {
        return  clientRepository.findAll().stream()
                .toList();
    }

    @Override
    public ClientEntity create(ClientEntity entity) {
        return clientRepository.save(entity);
    }

    @Override
    public Optional<ClientEntity> update(ClientEntity entity) {
        return Optional.of(clientRepository.saveAndFlush(entity));
    }

    @Override
    public void deleteById(UUID uuid) {
        clientRepository.findById(uuid)
            .map(entity -> {
                clientRepository.delete(entity);
                clientRepository.flush();
                return true;
            })
            .orElse(false);
    }

    private static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Override
    public void generateKeyPairForClients(ClientEntity buyerEntity,ClientEntity sellerEntity) throws NoSuchAlgorithmException {
        Optional<BankAccountEntity> actualBuyer=bankAccountService.findByUserId(buyerEntity.getId());
        Optional<BankAccountEntity> actualSeller=bankAccountService.findByUserId(sellerEntity.getId());
        if (actualBuyer.isPresent()&&actualSeller.isPresent()) {
            KeyPair keyPair = generateRsaKeyPair();
            sellerEntity.setOpenKey(String.valueOf(keyPair.getPublic()));
            buyerEntity.setOpenKey(String.valueOf(keyPair.getPublic()));
            actualBuyer.get().setClient(buyerEntity);
            actualSeller.get().setClient(sellerEntity);
            bankAccountService.update(actualBuyer.get());
            bankAccountService.update(actualSeller.get());
        }
    }

}
