package csd.api.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import csd.api.tables.*;
import csd.api.tables.templates.AccountRecord;
import csd.api.modules.user.*;
import csd.api.modules.account.*;


@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    
    @Mock
    private AccountRepository accounts;

    @Mock
    private TransRepository transfers;

    @Mock
    private CustomerRepository customers;

    @InjectMocks
    private AccountController accountController;

    @Test
    void createAccount_NewUser() {
        // Arrange
        AccountRecord newAccountRecord = new AccountRecord("NewRecord", 0, 0);
        Account newAccount = new Account();

        // Mock
        when(customers.existsByUsername(any(String.class))).thenReturn(true);

        when(accounts.save(any(Account.class))).thenReturn(newAccount);

        // Act
        Account savedAccount = accountController.createAccount(newAccountRecord);

        // Assert
        assertNotNull(savedAccount);
        verify(customers).existsByUsername(newAccountRecord.getUsername());
        verify(accounts).save(newAccount);
    }
}
