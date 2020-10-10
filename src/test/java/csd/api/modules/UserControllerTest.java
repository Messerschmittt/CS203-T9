package csd.api.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import csd.api.tables.*;
import csd.api.modules.user.*;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    

    @Mock
    private UserRepository users;
    
    @Mock
    private CustomerRepository customers;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserController userController;
    
    
    @Test
    void addUser_NewUsername_ReturnUser() {
        // arrange ***
        ApplicationUser user  =  new ApplicationUser("NewUser", "newuser1", "ROLE_USER");
        
        // mock the "findbytitle" operation
        when(users.existsByUsername(any(String.class))).thenReturn(false);
        // mock the "save" operation 
        when(users.save(any(ApplicationUser.class))).thenReturn(user);

        // act ***
        ApplicationUser savedUser = userController.addUser(user);
        
        // assert ***
        assertNotNull(savedUser);
        verify(users).existsByUsername(user.getUsername());
        verify(users).save(user);
    }

    @Test
    void addUser_SameUsername_ReturnNull() {
        ApplicationUser user  =  new ApplicationUser("NewUser", "newuser1", "ROLE_USER");
        ArrayList<ApplicationUser> list = new ArrayList<>();
        list.add(user);

        when(users.existsByUsername(any(String.class))).thenReturn(true);

        ApplicationUser savedUser = null;
        try {
            savedUser = userController.addUser(user);
        } catch (UsernameAlreadyTakenException e) {
            assertNull(savedUser);
            verify(users).existsByUsername(user.getUsername());
        } 
    }

    @Test
    void addCustomer_NewUsername_ReturnCustomer() {
        ApplicationUser user  =  new ApplicationUser("NewUser", "newuser1", "ROLE_USER");
        Customer customer = new Customer(user, "New User");

        when(users.existsByUsername(any(String.class))).thenReturn(true);
        when(customers.existsByUsername(any(String.class))).thenReturn(false);
        when(users.findByUsername(any(String.class))).thenReturn(user);
        when(customers.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = userController.addCustomer(customer);

        assertNotNull(savedCustomer);
        verify(users).existsByUsername(user.getUsername());
        verify(customers).existsByUsername(customer.getUsername());
        verify(users).findByUsername(user.getUsername());
        verify(customers).save(customer);
    }

    
     @Test
     void addCustomer_SameUsername_ReturnNull() {
        ApplicationUser user  =  new ApplicationUser("NewUser", "newuser1", "ROLE_USER");
        Customer customer = new Customer(user, "New User");

        when(users.existsByUsername(any(String.class))).thenReturn(true);
        when(customers.existsByUsername(any(String.class))).thenReturn(true);

        Customer savedCustomer = null; 
        
        try {
            savedCustomer =  userController.addCustomer(customer);
        } catch ( CustomerAlreadyExistsException e) {
            assertNull(savedCustomer);
            verify(users).existsByUsername(user.getUsername());
            verify(customers).existsByUsername(customer.getUsername());
        }
     }

     
     








}