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

	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // acquire the various instances we need
        JdbcTemplate template = ctx.getBean(JdbcTemplate.class);
        EmployeeRepository empRepo = ctx.getBean(EmployeeRepository.class);
        CustomerRepository cusRepo = ctx.getBean(CustomerRepository.class);
        ContentRepository contRepo = ctx.getBean(ContentRepository.class);
        UserRepository userRepo = ctx.getBean(UserRepository.class);
        AccountRepository acctRepo = ctx.getBean(AccountRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        StockRepository stockRepo = ctx.getBean(StockRepository.class);
        TradeRepository tradeRepo = ctx.getBean(TradeRepository.class);

        StockController stockController = new StockController(stockRepo, tradeRepo, acctRepo);

        List<ApplicationUser> initUsers = Arrays.asList(
            new ApplicationUser("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER"),
            new ApplicationUser("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST"),
            new ApplicationUser("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST"),
            new ApplicationUser(BANK_USERNAME, encoder.encode(BANK_PASSWORD), "ROLE_USER")
            );

        initUsers.forEach(user -> {
            System.out.println("[User Initialised]" + userRepo.save(user).getUsername());
        });


        /*
        List<ApplicationUser> initUsers = Arrays.asList(
            new ApplicationUser("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER"),
            new ApplicationUser("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST"),
            new ApplicationUser("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST"),
            new ApplicationUser("good_user_1", encoder.encode("01_user_01"), "ROLE_USER"),
            new ApplicationUser("good_user_2", encoder.encode("02_user_02"), "ROLE_USER"),
            new ApplicationUser(BANK_USERNAME, encoder.encode(BANK_PASSWORD), "ROLE_USER")
            );

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
        */
    
        List<Customer> initCustomer = Arrays.asList(
            new Customer(userRepo.findByUsername(BANK_USERNAME), BANK_FULL_NAME)
        );

        initCustomer.forEach(customer -> {
            customer.setPortfolio(new Portfolio(customer));
            System.out.println("[Customer Initialised]" + cusRepo.save(customer).getUsername());
        });
        

        List<Account> initAccount = Arrays.asList(
            new Account(cusRepo.findByUsername(BANK_USERNAME), BANK_BALANCE, BANK_AVAIL_BALANCE)
        );

        initAccount.forEach(account -> {
            System.out.println("[Account Initialised]" + acctRepo.save(account).getCustomer().getUsername());
        });
    
/*
        List<Content> initContent = Arrays.asList(
            new Content("Content Title 1", "Content Summary 1", "Content Content 1", "Content Link 1"),
            new Content("Content Title 2", "Content Summary 2", "Content Content 2", "Content Link 2")
        );
        initContent.forEach(content -> {
            contRepo.save(content);
            System.out.println("[Content Initialised]" + contRepo.save(content).getTitle());

            // System.out.println(contRepo.findById(customer.getId()));

        });
*/
        
        stockController.initialiseStock();
    }
}
