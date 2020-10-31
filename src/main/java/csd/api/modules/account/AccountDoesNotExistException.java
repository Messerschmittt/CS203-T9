package csd.api.modules.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class AccountDoesNotExistException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AccountDoesNotExistException(Integer id) {
        super("Could not find account " + id);
    }
    
}
