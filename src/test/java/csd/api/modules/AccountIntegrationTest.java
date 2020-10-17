package csd.api.modules;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import csd.api.tables.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AccountIntegrationTest {

    @LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accounts;

    @Autowired
    private UserRepository users;

    @Autowired
    private TransRepository transfers;

    @Autowired
    private CustomerRepository customers;

    @BeforeEach
    void init() {
        ApplicationUser user = users.save(new ApplicationUser("NewUser", "newuser123", "ROLE_USER"));
        customers.save(new Customer(user, "NewUser"));
        accounts.save(new Account(customers.findByUsername("NewUser"), 100000, 100000));

        users.save(new ApplicationUser("NewUser", "newuser123", "ROLE_USER"));
        
        users.save(new ApplicationUser("NewManager", "newmanager123", "ROLE_MANAGER"));

        users.save(new ApplicationUser("NewAnalyst", "newanalyst123", "ROLE_ANALYST"));

    }

    @AfterEach
    void tearDown() {
        users.deleteAll();
        customers.deleteAll();
        accounts.deleteAll();

    }

    // @Test
    // public void getAccounts_Success() throws Exception {
    //     URI uri = new URI(baseUrl + port + "/accounts");

    //     ResponseEntity<Account[]> result = restTemplate.withBasicAuth("NewManager", "newmanager123")
    //                             .exchange(uri, HttpMethod.GET, null, Account[].class);

    //     Account[] accountList = result.getBody();

    //     assertEquals(200, result.getStatusCode().value());
    //     assertEquals(5, accountList.length);
    // }

    // @Test
    // public void getAccountById_ValidAccountId_Success() throws Exception {

    // }
}
