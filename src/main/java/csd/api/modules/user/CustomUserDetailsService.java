package csd.api.modules.user;

import csd.api.tables.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        csd.api.tables.ApplicationUser applicationUser = users.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        System.out.println("" + applicationUser.getUsername() + " auth " + applicationUser.getAuthorities().size());
        return new User(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.getAuthorities());
    }
}


// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {
//     private UserRepository users;
    
//     public CustomUserDetailsService(UserRepository users) {
//         this.users = users;
//     }
//     @Override
//     public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException {
//         return users.findByUsername(username)
//             .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
//     }
    
// }