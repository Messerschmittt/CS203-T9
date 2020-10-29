package csd.api.modules.trading;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Resource created
public class InvalidTradeTiming extends Exception{
    
    private static final long serialVersionUID = 1L;

    public InvalidTradeTiming(){
        super("Market only open on weekday 9am to 5pm, this trade will be matched in next market open session");
    }
}
