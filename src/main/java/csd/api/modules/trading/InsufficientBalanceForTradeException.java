package csd.api.modules.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class InsufficientBalanceForTradeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceForTradeException(Integer id) {
        super("Insufficient funds for trade:  " + id);
    }

    public InsufficientBalanceForTradeException() {
        super("Insufficient funds for trade matching.");
    }
}
