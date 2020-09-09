package csd.api.modules.user;

import csd.api.tables.*;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private CustomerRepository customers;
    private EmployeeRepository employees;

    public UserController(EmployeeRepository employees, CustomerRepository customers){
        this.employees = employees;
        this.customers = customers;
    }

    /**
     * Return information of all customers
     * @return
     */
    @GetMapping("/customers")
    public List<Customer> getCustomers(){
        return customers.findAll();
    }

    /**
     * Return customer with the given id
     * Throws a CustomerNotFoundException if there is no customer with the given id
     * @param id
     * @return
     */
    @GetMapping("/customers/{id}")
    public Optional<Customer> getCustomer(@PathVariable Long id){
        Optional<Customer> customer = customers.findById(id);
        if(!customer.isPresent()){
            throw new CustomerNotFoundException(id);
        }
        return customer;
    }

    /**
     * Return information of all employees
     * @return
     */
    @GetMapping("/employees")
    public List<Employee> getEmployees(){
        return employees.findAll();
    }

    /**
     * Set the customerstatus
     * @param id
     * @param status
     * @return the customer record that was changed
     */
    @PostMapping("/empolyee/customerstatus/{id}/deactivate")
    public Customer setCustomerStatus(@PathVariable Long id){
        Optional<Customer> c = customers.findById(id);
        if(!c.isPresent()){
            throw new CustomerNotFoundException(id);
        }

        Customer customer = c.get();
        customer.setActive("false");
        return customers.save(customer);
    }

    
}