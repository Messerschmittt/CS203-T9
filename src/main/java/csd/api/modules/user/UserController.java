package csd.api.modules.user;

import csd.api.tables.*;

import java.util.List;
import java.util.*;

import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


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

    @GetMapping("/api/users")
    public List<ApplicationUser> getUsers() {
        return users.findAll();
    }

    @GetMapping("/api/customers")
    public List<Customer> getCustomers() {
        return customers.findAll();
    }

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/api/user/createUser")    
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
    @PostMapping("/api/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer addCustomer(@Valid @RequestBody Customer customer){
        String username = customer.getUsername();
        String authorities = customer.getAuthorities();
        String password = customer.getPassword();


        ApplicationUser user = users.findByUsername(username);

        try{
            if (!users.existsByUsername(username)) {
                user = new ApplicationUser(username, password, authorities);
                user.setPassword(encoder.encode(user.getPassword()));
                user = users.save(user);
            } 
        }catch(Exception e){
            throw new InvalidInputException();
        }
        
        

        if (customers.existsByUsername(username)) {
            throw new CustomerAlreadyExistsException(username);
        } 
        if (customers.existsByNric(customer.getNric())) {
            throw new CustomerAlreadyExistsException();
        }

        if (!customer.checkNRIC() && !customer.checkPhone()) {
            throw new InvalidInputException(customer.getNric(), customer.getPhone(), "nric", "phone number");
        } else if (!customer.checkNRIC()) {
            throw new InvalidInputException(customer.getNric(), "nric");
        } else if (!customer.checkPhone()) {
            throw new InvalidInputException(customer.getPhone(), "phone number");
        }

        Portfolio portfolio = new Portfolio(customer);
        customer.setPassword(password); 
        customer.setAuthorities(authorities);
        customer.setActive(true);
        customer.setApplication_User(user);
        customer.setPortfolio(portfolio);

        if (!customer.validate()) {
            throw new InvalidInputException();
        }

        return customers.save(customer);
    }

    @PostMapping("/api/login_page")
    public ApplicationUser loginUser(@RequestBody ApplicationUser user){
        ApplicationUser login = users.findByUsername(user.getUsername());
        return login;
    }

    @PostMapping("/api/logoutSuccess")
    @ResponseBody
    public String successLogout(){
        return "Successfully logged out";
    }


    @GetMapping("/api/customers/{id}")
    public Customer getCustomerDetails(@PathVariable Integer id, Authentication auth){
        Optional<Customer> customer = customers.findById(id);
        if(!customer.isPresent()){
            throw new CustomerNotFoundException(id);
        }
        Customer c = customer.get();

        if (auth == null) {
            System.out.println("HAHAGAHHAGA");
        }

        // System.out.println("get:"+ auth.getAuthorities().toString());
        // To ensure that customers can only view their own details and not other customers
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            
            if(!auth.getName().equals(c.getUsername())){
                throw new UnauthorisedUserException("other customers details");
            }
           
        }
        return customer.get();
    }

    @PutMapping("/api/customers/{id}")
    public Customer updateCustomer(@RequestBody Customer customer, @PathVariable Integer id, Authentication auth){
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("put:"+ auth.getAuthorities());
        Optional<Customer> c = customers.findById(id);
        if(!c.isPresent()){
            throw new CustomerNotFoundException(id);
        }

        Customer toUpdate = c.get();
        
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if (toUpdate != customers.findByUsername(auth.getName())) {
                throw new UnauthorisedUserException("or update other customers details");
            }
        }

        if (!customer.checkPhone() || !customer.validate()) {
            throw new InvalidInputException();
        }
        toUpdate.setPhone(customer.getPhone());
        toUpdate.setPassword(encoder.encode(customer.getPassword()));
        toUpdate.setAddress(customer.getAddress());
        
        // Only allow updating of active for manager
        if(auth.getAuthorities().toString().equals("[ROLE_MANAGER]")){
            // toUpdate.setFull_name(customer.getFull_name());
            // toUpdate.setNric(customer.getNric());
            // toUpdate.setAuthorities(customer.getAuthorities());
            toUpdate.setActive(customer.getActive());
        }

        return customers.save(toUpdate);
    }
   
    
}