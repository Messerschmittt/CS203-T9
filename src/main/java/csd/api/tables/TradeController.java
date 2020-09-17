package csd.api.tables;

import csd.api.tables.*;

import java.util.List;
import java.util.Optional;


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

@RestController
public class TradeController {
    private TradeRepository trades;

    public TradeController(TradeRepository trades){
        this.trades = trades;
    }

     /**
     * List all trades in the system
     * @return list of all trades
     */
    @GetMapping("/trades")
    public List<Trade> getTrade(){
        return trades.findAll();
    }
    /**
     * Add a new trade with POST request to "/trades"
     * Note the use of @RequestBody
     * @param Trade
     * @return list of all trades
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/trades")
    public Trade addTrade(@RequestBody Trade trade) {
        return trades.save(trade);
    }


}
