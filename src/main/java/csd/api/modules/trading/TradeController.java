package csd.api.modules.trading;

import csd.api.tables.*;
import csd.api.tables.templates.*;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;

import csd.api.modules.user.*;
import csd.api.modules.account.*;
import csd.api.modules.trading.*;

@RestController
public class TradeController {
    private TradeService tradeService;
    private AccountRepository accRepo;

    public TradeController(TradeService tradeService){
        this.tradeService = tradeService;
    }

     /**
     * List all trades in the system
     * @return list of all trades
     */
    @GetMapping("/trades")
    public List<Trade> getTrades(){
        return tradeService.listTrades();
    }

    //for ROLE_USER
    /**
     * Search for trade with the given id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     * @return trade with the given id
     */
    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Integer id,Authentication auth){
        Trade trade = tradeService.getTrade(id);
        
        // To handle "trade not found" error using proper HTTP status code: 404
        if(trade == null) throw new TradeNotFoundException(id);
        //-> need to check is this specific trade belong to the login user?-------------
        Account cusAcc = accRepo.findById(trade.getAccount().getId()).get();
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if(!auth.getName().equals(cusAcc.getCustomer().getUsername())){
                throw new UnauthorisedAccountAccessException(id);
            }
        }

        return tradeService.getTrade(id);
    }

    /**
     * Remove a trade with the DELETE request to "/trades/{id}"
     * For ROLE_USER only
     * If there is no trade with the given "id", will throw a TradeNotFoundException
     * @param id
     */
    @DeleteMapping("/trades/{id}")
    public void deleteTrade(@PathVariable Integer id, Authentication auth){
        Trade trade = tradeService.getTrade(id);
        
        // To handle "trade not found" error using proper HTTP status code: 404
        if(trade == null) throw new TradeNotFoundException(id);
        //-> need to check is this specific trade belong to the login user?-------------
        Account cusAcc = accRepo.findById(trade.getAccount().getId()).get();
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if(!auth.getName().equals(cusAcc.getCustomer().getUsername())){
                throw new UnauthorisedAccountAccessException(id);
            }
        }

        tradeService.deleteTrade(id);
    }
    
    /**
     * Create trade via the POST request to "/trades"
     * @param tradeRecord
     * @return the latest info of trade
     */
    @PostMapping("/trades")
    public Trade TradeGenerate(@RequestBody TradeRecord tradeRecord, Authentication auth){
         // Only allow role_user of create trade
         if(!auth.getAuthorities().toString().equals("[ROLE_USER]")){
            throw new UnauthorisedUserException("trade");
        }

        return tradeService.TradeGenerate(tradeRecord);
    }
    
    // //--can change to normal function (no need mapping)
    // @GetMapping("/trades/{action}/{date}/{symbol}")
    // public List<Trade> getAllmatchingorder(@PathVariable String action,@PathVariable String date,@PathVariable String symbol) {
    //     return tradeRepo.findByActionAndDateAndSymbol(action,date,symbol);
    // }

    // //--can change to normal function (no need mapping)
    // @GetMapping("/trades/{action}/{status}/{symbol}")
    // public List<Trade> getAllvalidorder(@PathVariable String action,@PathVariable String status,@PathVariable String symbol) {
    //     return tradeRepo.findByActionAndStatusAndSymbol(action,status, symbol);
    // }
}

