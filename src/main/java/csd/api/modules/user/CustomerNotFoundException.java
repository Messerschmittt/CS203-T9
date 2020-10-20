package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class CustomerNotFoundException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(Integer id) {
        super("Could not find customer " + id);
    }

    public CustomerNotFoundException(String username) {
        super("Could not find customer with username " + username);
    }
    
}
