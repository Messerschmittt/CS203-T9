package csd.api.modules.trading;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class RyverBankAccountException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RyverBankAccountException() {
        super("There is an error with the Ryver Bank account");
    }
}
