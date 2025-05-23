package org.example.bank.repository;

import org.example.bank.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    Optional<ClientEntity> findById(UUID uuid);

    Optional<ClientEntity> findByName(String name);

    List<ClientEntity> findAll();
}
