package csd.api.modules.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class ExceedAvailableBalanceException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ExceedAvailableBalanceException(Long id) {
        super("Attempted transfer amount exceeds the available balance for account " + id);
    }
    
}
