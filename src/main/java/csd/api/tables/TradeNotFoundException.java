package csd.api.tables;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class TradeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TradeNotFoundException(Integer id) {
        super("Could not find trade " + id);
    }
}
