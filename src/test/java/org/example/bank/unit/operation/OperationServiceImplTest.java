package org.example.bank.unit.operation;

import com.fdkost.jee.soap.Client;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.entity.OperationEntity;
import org.example.bank.model.PaymentModel;
import org.example.bank.model.PaymentOperationResult;
import org.example.bank.repository.OperationRepository;
import org.example.bank.service.bankaccount.BankAccountService;
import org.example.bank.service.operation.OperationServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OperationServiceImplTest {

    @Mock
    private static PaymentModel model;
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private OperationRepository operationRepository;

    private OperationServiceImpl operationService;
    private static BankAccountEntity buyerTestBankAccount;
    private static BankAccountEntity sellerTestBankAccount;
    private static Client testBuyer;
    private static Client testSeller;
    private static OperationEntity testOperation;

    @BeforeClass
    public static void setUpBeforeClass() {
        testBuyer = new Client();
        testBuyer.setId(UUID.randomUUID().toString());
        testBuyer.setName("Buyer");
        testSeller = new Client();
        testSeller.setId(UUID.randomUUID().toString());
        testSeller.setName("Seller");

        buyerTestBankAccount = new BankAccountEntity();
        buyerTestBankAccount.setClient(ClientEntity.builder()
                .id(UUID.fromString(testBuyer.getId()))
                .name(testBuyer.getName())
                .build());
        buyerTestBankAccount.setNumber("7777");
        buyerTestBankAccount.setId(UUID.randomUUID());

        sellerTestBankAccount = new BankAccountEntity();
        sellerTestBankAccount.setClient(ClientEntity.builder()
                .id(UUID.fromString(testSeller.getId()))
                .name(testSeller.getName())
                .build());
        sellerTestBankAccount.setNumber("8888");
        sellerTestBankAccount.setId(UUID.randomUUID());

        testOperation = new OperationEntity();
        testOperation.setId(UUID.randomUUID());
        testOperation.setSum(BigDecimal.valueOf(100));
        testOperation.setSenderBankAccount(buyerTestBankAccount);
        testOperation.setRecipientBankAccount(sellerTestBankAccount);
        testOperation.setTransaction(UUID.randomUUID());

    }

    @Before
    public void init() {
        operationService = new OperationServiceImpl(operationRepository, bankAccountService);
    }

    @Test
    public void findById() {
        when(operationRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(testOperation));

        Optional<OperationEntity> resultOperation = operationService.findById(testOperation.getId());

        Assert.assertNotNull(resultOperation);
        Assert.assertEquals(testOperation, operationService.findById(testOperation.getId()).get());

    }

    @Test
    public void update() {
        when(operationService.create(any(OperationEntity.class))).thenReturn(testOperation);
        operationService.create(testOperation);

        testOperation.setSum(BigDecimal.valueOf(200));

        when(operationRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(testOperation));
        when(operationRepository.saveAndFlush(any(OperationEntity.class))).thenReturn(testOperation);

        Optional<OperationEntity> resultOperation = operationService.update(testOperation);

        Assert.assertNotNull(resultOperation);
        Assert.assertEquals(testOperation, resultOperation.get());
    }

    @Test
    public void fillGetBank() {
        buyerTestBankAccount.setSum(BigDecimal.valueOf(100));
        sellerTestBankAccount.setSum(BigDecimal.valueOf(200));

        // when(bankAccountService.update(buyerTestBankAccount)).thenReturn(Optional.ofNullable(buyerTestBankAccount));
        // when(bankAccountService.update(sellerTestBankAccount)).thenReturn(Optional.ofNullable(sellerTestBankAccount));

        PaymentOperationResult resultOperation = operationService.fillGetBank(buyerTestBankAccount, sellerTestBankAccount,
                BigDecimal.valueOf(50), model);

        Assert.assertNotNull(resultOperation);
        Assert.assertEquals(200, resultOperation.getStatus());
    }
}
