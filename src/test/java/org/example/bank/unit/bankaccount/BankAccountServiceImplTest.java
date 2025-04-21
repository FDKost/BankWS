package org.example.bank.unit.bankaccount;

import com.fdkost.jee.soap.BuyerBankAccount;
import com.fdkost.jee.soap.Client;
import org.example.bank.entity.BankAccountEntity;
import org.example.bank.entity.ClientEntity;
import org.example.bank.repository.BankAccountRepository;
import org.example.bank.service.bankaccount.BankAccountServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    private BankAccountServiceImpl bankAccountService;
    private static BankAccountEntity testBankAccount;
    private static ClientEntity testClient;
    private static BuyerBankAccount testBuyer;
    private static Client testXMLClient;

    @BeforeClass
    public static void setUpBeforeClass() {
        testClient = new ClientEntity();
        testClient.setId(UUID.randomUUID());
        testClient.setName("Test");

        testXMLClient = new Client();
        testXMLClient.setId(UUID.randomUUID().toString());
        testXMLClient.setName("Test");

        testBankAccount = new BankAccountEntity();
        testBankAccount.setSum(BigDecimal.valueOf(0));
        testBankAccount.setId(UUID.randomUUID());
        testBankAccount.setNumber("7777");
        testBankAccount.setClient(testClient);

        testBuyer = new BuyerBankAccount();
        testBuyer.setId(UUID.randomUUID().toString());
        testBuyer.setNumber("7777");
        testBuyer.setClient(testXMLClient);
        testBuyer.setSum(BigDecimal.valueOf(0));
    }

    @Before
    public void setUp() {
        bankAccountService = new BankAccountServiceImpl(bankAccountRepository);
    }

    @Test
    public void findByUserIdTest() {
        when(bankAccountRepository.findByClientId(testClient.getId())).thenReturn(Optional.ofNullable(testBankAccount));

        Optional<BankAccountEntity> result = bankAccountService.findByUserId(testClient.getId());

        Assert.assertEquals(testBankAccount, result.get());
    }

    @Test
    public void updateTest() {
        Optional<BankAccountEntity> result = Optional.ofNullable(testBankAccount);
        result.get().setSum(BigDecimal.ONE);
        when(bankAccountRepository.saveAndFlush(testBankAccount)).thenReturn(result.get());

        Optional<BankAccountEntity> resultOperation = bankAccountService.update(result.get());

        Assert.assertNotEquals(testBankAccount, resultOperation);
    }

    @Test
    public void convertXMLBankAccountTest() {
        BankAccountEntity result = bankAccountService.convertBuyerBankAccount(testBuyer);

        Assert.assertEquals(testBankAccount.getNumber(), result.getNumber());
    }
}
