package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Error
public class UnauthorisedUserException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public UnauthorisedUserException(String str) {
        super("User has no authorization to access " + str );
    }
}


