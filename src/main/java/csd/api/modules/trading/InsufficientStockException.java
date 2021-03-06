package csd.api.modules.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class InsufficientStockException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InsufficientStockException() {
        super("The customer does not have enough stock quantity to fullfil the order.");
    }
}
