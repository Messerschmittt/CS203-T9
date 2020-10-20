package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 Error
public class CustomerNotActiveException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public CustomerNotActiveException() {
        super();
    }


    
}
