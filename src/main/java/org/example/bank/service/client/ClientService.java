package org.example.bank.service.client;

import org.example.bank.entity.ClientEntity;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    Optional<ClientEntity> findById(UUID uuid);

    Optional<ClientEntity> findByName(String name);

    List<ClientEntity> findAll();

    ClientEntity create(ClientEntity entity);

    Optional<ClientEntity> checkClientExists(String name);

    Optional<ClientEntity> update(ClientEntity entity);

    void generateKeyPairForClients(ClientEntity buyerEntity, ClientEntity sellerEntity) throws NoSuchAlgorithmException;

    void deleteById(UUID uuid);

    Boolean checkClientsExists(String buyerName, String sellerName);
}
