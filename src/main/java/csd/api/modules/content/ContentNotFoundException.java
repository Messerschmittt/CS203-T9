package csd.api.modules.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
public class ContentNotFoundException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ContentNotFoundException(Integer id) {
        super("Could not find content" + id);
    }
    
}
