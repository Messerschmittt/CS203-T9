package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 400 Error
public class CustomerAlreadyExistsException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CustomerAlreadyExistsException(String username) {
        super(username + " already has a customer account");
    }

    public CustomerAlreadyExistsException() {
        super();
    }


    
}
