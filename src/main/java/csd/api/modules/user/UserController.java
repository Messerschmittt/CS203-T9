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

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/user/createUser")
    public ApplicationUser addUser(@Valid @RequestBody ApplicationUser user){
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
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
    public Customer getCustomerDetails(@PathVariable Long id){
        return customers.findByApplicationUserId(id).get(0);
    }
   
}