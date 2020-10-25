package csd.api;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import csd.api.modules.trading.StockController;
import csd.api.tables.*;
import static csd.api.modules.account.RyverBankAccountConstants.*;

@SpringBootApplication
public class Application {

    private static ApplicationContext ctx;
    private static JdbcTemplate template;
    private static EmployeeRepository empRepo;
    private static CustomerRepository cusRepo;
    private static ContentRepository contRepo;
    private static UserRepository userRepo;
    private static AccountRepository acctRepo;
    private static BCryptPasswordEncoder encoder;
    private static StockRepository stockRepo;
    private static TradeRepository tradeRepo;

    private static StockController stockController;

    public static void initialise() {
        // List<ApplicationUser> initUsers = Arrays.asList(
        // new ApplicationUser("manager_1", encoder.encode("01_manager_01"),
        // "ROLE_MANAGER"),
        // new ApplicationUser("analyst_1", encoder.encode("01_analyst_01"),
        // "ROLE_ANALYST"),
        // new ApplicationUser("analyst_2", encoder.encode("02_analyst_02"),
        // "ROLE_ANALYST"),
        // new ApplicationUser(BANK_USERNAME, encoder.encode(BANK_PASSWORD),
        // "ROLE_USER")
        // );

        // initUsers.forEach(user -> {
        // System.out.println("[User Initialised]" + userRepo.save(user).getUsername());
        // });

        List<ApplicationUser> initUsers = Arrays.asList(
                new ApplicationUser("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER"),
                new ApplicationUser("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST"),
                new ApplicationUser("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST"),
                // new ApplicationUser("good_user_1", encoder.encode("01_user_01"),
                // "ROLE_USER"),
                // new ApplicationUser("good_user_2", encoder.encode("02_user_02"),
                // "ROLE_USER"),
                new ApplicationUser(BANK_USERNAME, encoder.encode(BANK_PASSWORD), "ROLE_USER"));

        initUsers.forEach(user -> {
            System.out.println("[User Initialised]" + userRepo.save(user).getUsername());
        });

        List<Employee> initEmployee = Arrays.asList(
        // new Employee(userRepo.findByUsername("manager_1"), "Manager 1"),
        // new Employee(userRepo.findByUsername("analyst_1"), "Analyst 1"),
        // new Employee(userRepo.findByUsername("analyst_2"), "Analyst 2")

        );

        initEmployee.forEach(employee -> {
            // empRepo.save(employee);
            System.out.println("[User Initialised]" + empRepo.save(employee).getApplicationUser().getUsername());

        });

        List<Customer> initCustomer = Arrays.asList(new Customer(userRepo.findByUsername(BANK_USERNAME), BANK_FULL_NAME)
        // new Customer(userRepo.findByUsername("good_user_1"), "01_user_01"),
        // new Customer(userRepo.findByUsername("good_user_2"), "02_user_02")
        );

        initCustomer.forEach(customer -> {
            customer.setPortfolio(new Portfolio(customer));
            System.out.println("[Customer Initialised]" + cusRepo.save(customer).getUsername());
        });

        List<Account> initAccount = Arrays
                .asList(new Account(cusRepo.findByUsername(BANK_USERNAME), BANK_BALANCE, BANK_AVAIL_BALANCE)
                // new Account(cusRepo.findByUsername("good_user_1"),500000,500000),
                // new Account(cusRepo.findByUsername("good_user_2"),100000,100000)
                );

        initAccount.forEach(account -> {
            System.out.println("[Account Initialised]" + acctRepo.save(account).getCustomer().getUsername());
        });

        BANK_CUSTOMER = cusRepo.findByUsername(BANK_USERNAME);
        System.out.println("BANK_CUSTOMER Set - " + BANK_CUSTOMER.getUsername());
        BANK_ACCOUNT = acctRepo.findByCustomer_Id(BANK_CUSTOMER.getId());

        stockController.initialiseStock();
    }

    public static void main(String[] args) {
        ctx = SpringApplication.run(Application.class, args);
        // acquire the various instances we need
        template = ctx.getBean(JdbcTemplate.class);
        empRepo = ctx.getBean(EmployeeRepository.class);
        cusRepo = ctx.getBean(CustomerRepository.class);
        contRepo = ctx.getBean(ContentRepository.class);
        userRepo = ctx.getBean(UserRepository.class);
        acctRepo = ctx.getBean(AccountRepository.class);
        encoder = ctx.getBean(BCryptPasswordEncoder.class);
        stockRepo = ctx.getBean(StockRepository.class);
        tradeRepo = ctx.getBean(TradeRepository.class);

        stockController = new StockController(stockRepo, tradeRepo, acctRepo);
		initialise();
    }
}
