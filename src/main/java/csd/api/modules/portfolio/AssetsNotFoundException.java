package csd.api.modules.portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssetsNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public AssetsNotFoundException(Integer id) {
        super("Could not find Assets of ID: " + id);
    }

    public AssetsNotFoundException(String symbol) {
        super("Could not find Assets of stock " + symbol);
    }

    public AssetsNotFoundException() {
        super("There is no stock in assets");
    }
    
}
