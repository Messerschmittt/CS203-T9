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
        assets.deleteAll();
        customers.deleteAll();

        Application.initialise();

    }

    
}
