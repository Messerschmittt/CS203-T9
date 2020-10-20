package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


// may not be used, consider removing

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class UserNotFoundException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username) {
        super("Could not find user " + username);
    }

    public UserNotFoundException() {
        super("Could not find user ");
    }
    
}
