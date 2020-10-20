package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Error
public class UsernameAlreadyTakenException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UsernameAlreadyTakenException(String username) {
        super( username + " is already taken. Please choose another username");
    }


    
}
