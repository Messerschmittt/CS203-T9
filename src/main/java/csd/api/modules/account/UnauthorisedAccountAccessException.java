package csd.api.modules.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Error
public class UnauthorisedAccountAccessException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnauthorisedAccountAccessException(Integer id) {
        super("Access to this account " + id + " is unauthorised");
    }
    
}
