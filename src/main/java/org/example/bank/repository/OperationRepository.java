package org.example.bank.repository;

import org.example.bank.entity.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {
    Optional<OperationEntity> findById(UUID id);
    Optional<OperationEntity> findByTransaction(UUID transactionId);
}
