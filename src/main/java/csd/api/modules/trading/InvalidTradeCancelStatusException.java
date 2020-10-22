package csd.api.modules.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class InvalidTradeCancelStatusException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidTradeCancelStatusException(String status) {
        super("The customer is unable to cancel the "+ status + " trade");
    }
}

