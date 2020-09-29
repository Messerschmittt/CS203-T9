package csd.api.modules.user;

import csd.api.tables.*;

import java.util.List;
import java.util.*;

import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/user/createUser/customer")    
    public Customer addCustomer(@Valid @RequestBody Customer customer){
        String username = customer.getUsername();
        if (!users.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        }

        if (customers.existsByUsername(username)) {
            throw new CustomerAlreadyExistsException(username);
        }

        if (!customer.checkNRIC()) {
            throw new InvalidInputException(customer.getNric(), "nric");
        }

        if (!customer.checkPhone()) {
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
    public Customer getCustomerDetails(@PathVariable Integer id){
        // Optional<ApplicationUser> user = users.findById(id);
        // System.out.println(user.get().getUsername());


        // // Customer found = customers.findByApplication_User_Id(id);
        // // System.out.println(found.getUsername());
        // // return customers.findByApplication_User_Id(id);
        // if (user == null) {
        //     System.out.println("A");
        //     return null;
        // }
        Optional<Customer> customer = customers.findById(id);
        if (customer == null) {
            return null;
        }
        return customer.get();
    }
   
    
}