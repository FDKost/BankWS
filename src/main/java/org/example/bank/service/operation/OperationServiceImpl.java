package org.example.bank.service.operation;

import lombok.RequiredArgsConstructor;
import org.example.bank.entity.OperationEntity;
import org.example.bank.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationServiceImpl implements OperationService {
    private final OperationRepository operationRepository;
    @Override
    public Optional<OperationEntity> findById(UUID id) {
        return operationRepository.findById(id);
    }

    @Override
    public Optional<OperationEntity> findByTransaction(UUID transactionId) {
        return operationRepository.findByTransaction(transactionId);
    }

    @Override
    public OperationEntity create(OperationEntity operation) {
        return operationRepository.save(operation);
    }

    @Override
    public Optional<OperationEntity> update(OperationEntity operation) {
        return operationRepository.findById(operation.getId())
                .map(operationRepository::saveAndFlush);
    }

    @Override
    public void deleteById(OperationEntity operation) {
        operationRepository.findById(operation.getId())
                .map(entity ->{
                    operationRepository.delete(entity);
                    operationRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
