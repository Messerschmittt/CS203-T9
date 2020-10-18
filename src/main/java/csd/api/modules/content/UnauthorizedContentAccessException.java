package csd.api.modules.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnauthorizedContentAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedContentAccessException(Integer id) {
        super("Content not found" + id);
    }
    
}
