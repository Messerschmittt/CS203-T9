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

    //for ROLE_USER
    /**
     * Search for trade with the given id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     * @return book with the given id
     */
    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Long id){
        Optional<Trade> trade = trades.findById(id);
        if(!trade.isPresent()){
            throw new TradeNotFoundException(id);
        }

        Trade t = trade.get();
        return t;
    }

    /**
     * Remove a trade with the DELETE request to "/trades/{id}"
     * If there is no trade with the given "id", throw a BookNotFoundException
     * @param id
     */
    @DeleteMapping("/trades/{id}")
    public void deleteTrade(@PathVariable Long id){
        if(!trades.existsById(id)) {
            throw new TradeNotFoundException(id);
        }

        trades.deleteById(id);
    }

    @GetMapping("/trades/{action}/{orderdate}/{symbol}")
    public List<Trade> getAllmatchingorder(@PathVariable String action,@PathVariable String orderdate,@PathVariable String symbol) {
        return trades.findByActionAndOrderdateAndSymbol(action,orderdate, symbol);
    }
}
