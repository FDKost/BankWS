package org.example.bank.repository;

import org.example.bank.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
    Optional<BankAccountEntity> findById(UUID bankAccountId);
    Optional<BankAccountEntity> findByClientId(UUID userId);

}
