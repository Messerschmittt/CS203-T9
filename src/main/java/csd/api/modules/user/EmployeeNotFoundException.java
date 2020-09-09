package csd.api.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class EmployeeNotFoundException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(Long id) {
        super("Could not find employee " + id);
    }
    
}
