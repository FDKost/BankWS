package org.example.bank.unit.client;

import org.example.bank.entity.ClientEntity;
import org.example.bank.repository.ClientRepository;
import org.example.bank.service.bankaccount.BankAccountService;
import org.example.bank.service.client.ClientServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceImplTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private BankAccountService bankAccountService;

    private ClientServiceImpl clientService;
    private static ClientEntity testClient;

    @BeforeClass
    public static void setUpBeforeClass() {
        testClient = new ClientEntity();
        testClient.setId(UUID.randomUUID());
        testClient.setName("test");
    }

    @Before
    public void setUp() {
        clientService = new ClientServiceImpl(clientRepository, bankAccountService);
    }

    @Test
    public void findByIdTest() {
        when(clientRepository.findById(testClient.getId())).thenReturn(Optional.ofNullable(testClient));

        Optional<ClientEntity> resultClient = clientService.findById(testClient.getId());

        Assert.assertEquals(testClient, resultClient.get());
    }

    @Test
    public void checkClientExistsTest() {
        when(clientRepository.findByName(testClient.getName())).thenReturn(Optional.ofNullable(testClient));

        Optional<ClientEntity> resultClient = clientService.checkClientExists(testClient.getName());
        Assert.assertEquals(testClient, resultClient.get());
    }
}
