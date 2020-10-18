package csd.api.modules.user;

import csd.api.tables.*;

import java.util.List;
import java.util.*;

import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
public class UserController {
    private UserRepository users;
    private CustomerRepository customers;
    private PortfolioRepository portfolios;
    private BCryptPasswordEncoder encoder;

    public UserController(UserRepository users, BCryptPasswordEncoder encoder, CustomerRepository customers){
        this.users = users;
        this.encoder = encoder;
        this.customers = customers;
    }

    @GetMapping("/users")
    public List<ApplicationUser> getUsers() {
        return users.findAll();
    }

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customers.findAll();
    }

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/user/createUser")    
    public ApplicationUser addUser(@Valid @RequestBody ApplicationUser user){
        String username = user.getUsername();
        if (users.existsByUsername(username)) {
            throw new UsernameAlreadyTakenException(username);
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return users.save(user);
    }

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/customers")    
    public Customer addCustomer(@Valid @RequestBody Customer customer){
        String username = customer.getUsername();
        if (!users.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        }

        if (customers.existsByUsername(username)) {
            throw new CustomerAlreadyExistsException(username);
        }

        if (!customer.checkNRIC() && !customer.checkPhone()) {
            throw new InvalidInputException(customer.getNric(), customer.getPhone(), "nric", "phone number");
        } else if (!customer.checkNRIC()) {
            throw new InvalidInputException(customer.getNric(), "nric");
        } else if (!customer.checkPhone()) {
            throw new InvalidInputException(customer.getPhone(), "phone number");
        }

        Portfolio portfolio = new Portfolio(customer);
        ApplicationUser user = users.findByUsername(username);
        customer.setPassword(user.getPassword()); 
        // if incorrect password was typed, wont matter as it is overwritten
        customer.setAuthorities(user.getSimpleAuthorities());
        customer.setActive(true);
        customer.setApplication_User(user);
        customer.setPortfolio(portfolio);

        return customers.save(customer);
    }

    @PostMapping("/login_page")
    public ApplicationUser loginUser(@RequestBody ApplicationUser user){
        ApplicationUser login = users.findByUsername(user.getUsername());
        return login;
    }

    @PostMapping("/logoutSuccess")
    @ResponseBody
    public String successLogout(){
        return "Successfully logged out";
    }


    @GetMapping("/customers/{id}")
    public Customer getCustomerDetails(@PathVariable Integer id, Authentication auth){
        Optional<Customer> customer = customers.findById(id);
        if(!customer.isPresent()){
            throw new CustomerNotFoundException(id);
        }

        // To ensure that customers can only view their own details and not other customers
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            Customer c = customers.findByUsername(auth.getName());
            if (c.getId() == id) {
                return customer.get();
            } else {
                throw new UnauthorisedUserException("other customers details");
            }
        }
        return customer.get();
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@RequestBody Customer customer, @PathVariable Integer id, Authentication auth){
        Optional<Customer> c = customers.findById(customer.getId());
        if(!c.isPresent()){
            throw new CustomerNotFoundException(customer.getId());
        }

        Customer toUpdate = c.get();

        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if (toUpdate != customers.findByUsername(auth.getName())) {
                throw new UnauthorisedUserException("or update other customers details");
            }
        }

        
        toUpdate.setPhone(customer.getPhone());
        toUpdate.setPassword(encoder.encode(customer.getPassword()));
        toUpdate.setAddress(customer.getAddress());
        
        // Only allow updating of the rest of the fields for manager
        if(auth.getAuthorities().toString().equals("[ROLE_MANAGER]")){
            toUpdate.setFull_name(customer.getFull_name());
            toUpdate.setNric(customer.getNric());
            toUpdate.setAuthorities(customer.getAuthorities());
        }

        return customers.save(toUpdate);
    }
   
    
}