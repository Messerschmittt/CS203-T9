package csd.api;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;


import csd.api.tables.*;
import csd.api.modules.user.*;
import csd.api.modules.account.*;
import csd.api.modules.content.*;


@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // acquire the various instances we need
        JdbcTemplate template = ctx.getBean(JdbcTemplate.class);
        EmployeeRepository empRepo = ctx.getBean(EmployeeRepository.class);
        CustomerRepository cusRepo = ctx.getBean(CustomerRepository.class);
        ContentRepository contRepo = ctx.getBean(ContentRepository.class);

        List<Employee> initEmployee = Arrays.asList(
            new Employee("Manager 1", "ROLE_MANAGER", "manager_1", "01_manager_01"),
            new Employee("Analyst 1", "ROLE_ANALYST", "analyst_1", "01_analyst_01"),
            new Employee("Analyst 2", "ROLE_ANALYST", "analyst_2", "02_analyst_02")
        );

        initEmployee.forEach(employee -> {
            empRepo.save(employee);
        });

        List<Customer> initCustomer = Arrays.asList(
            new Customer("Customer 1", "ROLE_CUSTOMER", "good_user_1", "01_user_01"),
            new Customer("Customer 2", "ROLE_CUSTOMER", "good_user_2", "01_user_02")
        );

        initCustomer.forEach(customer -> {
            cusRepo.save(customer);
        });

        List<Content> initContent = Arrays.asList(
            new Content("Content Title 1", "Content Summary 1", "Content Content 1", "Content Link 1"),
            new Content("Content Title 2", "Content Summary 2", "Content Content 2", "Content Link 2")
        );
        initContent.forEach(content -> {
            contRepo.save(content);
        });

    }
    
}
