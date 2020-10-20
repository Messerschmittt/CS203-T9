package csd.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import csd.api.tables.*;
import static csd.api.modules.account.RyverBankAccountConstants.*;
import csd.api.modules.trading.StockController;



@RestController
public class SystemController {
    private AccountRepository accounts;
    private AssetsRepository assets;
    private ContentRepository contents;
    private CustomerRepository customers;
    private EmployeeRepository employees;
    private PortfolioRepository portfolios;
    private StockRepository stocks;
    private TradeRepository trades;
    private TransRepository trans;
    private UserRepository users;
    private StockController stockController;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public SystemController( AccountRepository accounts, AssetsRepository assets,
     ContentRepository contents, CustomerRepository customers, EmployeeRepository employees,
     PortfolioRepository portfolios, StockRepository stocks, TradeRepository trades,
     TransRepository trans, UserRepository users, StockController stockController, BCryptPasswordEncoder encoder){
        this.accounts = accounts;
        this.assets = assets;
        this.contents = contents;
        this.customers = customers;
        this.employees = employees;
        this.portfolios = portfolios;
        this.stocks = stocks;
        this.trades = trades;
        this.trans = trans;
        this.users = users;
        this.stockController = stockController;
        this.encoder = encoder;
    }

    @PostMapping("/reset")
    public void resetEntireSystem(){
        accounts.deleteAll();
        contents.deleteAll();
        employees.deleteAll();
        portfolios.deleteAll();
        stocks.deleteAll();
        trades.deleteAll();
        trans.deleteAll();
        users.deleteAll();

        List<ApplicationUser> initUsers = Arrays.asList(
            new ApplicationUser("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER"),
            new ApplicationUser("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST"),
            new ApplicationUser("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST"),
            new ApplicationUser(BANK_USERNAME, encoder.encode(BANK_PASSWORD), "ROLE_USER")
            );

        initUsers.forEach(user -> {
            System.out.println("[User Initialised]" + users.save(user).getUsername());
        });

        List<Customer> initCustomer = Arrays.asList(
            new Customer(users.findByUsername(BANK_USERNAME), BANK_FULL_NAME)
        );

        initCustomer.forEach(customer -> {
            customer.setPortfolio(new Portfolio(customer));
            System.out.println("[Customer Initialised]" + customers.save(customer).getUsername());
        });
        

        List<Account> initAccount = Arrays.asList(
            new Account(customers.findByUsername(BANK_USERNAME), BANK_BALANCE, BANK_AVAIL_BALANCE)
        );

        initAccount.forEach(account -> {
            System.out.println("[Account Initialised]" + accounts.save(account).getCustomer().getUsername());
        });
    

        stockController.initialiseStock();

    }

    
}
