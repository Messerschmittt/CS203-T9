package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class InvalidInputException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String input, String type) {
        super( input + " is not a valid " + type);
    }

    public InvalidInputException(String input1, String input2, String type1, String type2) {
        super( input1 + " is not a valid " + type1 + " and "  + input2 + " is not a valid " + type2);
    }

    public InvalidInputException(){
        super();
    }
    
}
