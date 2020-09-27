package csd.api.tables;

import csd.api.tables.*;

import java.util.List;
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


import csd.api.modules.account.AccountNotFoundException;
import csd.api.modules.account.ExceedAvailableBalanceException;
import csd.api.tables.TradeRepository;
import csd.api.tables.Account;
import csd.api.tables.AccountRepository;
import csd.api.tables.Trade;
import csd.api.tables.TradeController;
import csd.api.tables.Stock;
import csd.api.tables.StockRepository;
import csd.api.tables.Trans;
import csd.api.modules.account.AccountController;

@RestController
public class TradeController {
    private TradeRepository tradeRepo;
    private AccountRepository accRepo;
    private TradeController tradesController;
    private StockRepository stockRepo;
    private AccountController accController;

    public TradeController(TradeRepository trades, AccountRepository accRepo, StockRepository stockRepo){
        this.tradeRepo = trades;
        this.accRepo = accRepo;
        this.stockRepo = stockRepo;
    }

     /**
     * List all trades in the system
     * @return list of all trades
     */
    @GetMapping("/trades")
    public List<Trade> getTrade(){
        return tradeRepo.findAll();
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
        return tradeRepo.save(trade);
    }

    //for ROLE_USER
    /**
     * Search for trade with the given id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     * @return book with the given id
     */
    @GetMapping("/trades/{id}")
    public Trade getTrade(@PathVariable Integer id){
        Optional<Trade> trade = tradeRepo.findById(id);
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
    public void deleteTrade(@PathVariable Integer id){
        if(!tradeRepo.existsById(id)) {
            throw new TradeNotFoundException(id);
        }

        tradeRepo.deleteById(id);
    }

    @GetMapping("/trades/{action}/{date}/{symbol}")
    public List<Trade> getAllmatchingorder(@PathVariable String action,@PathVariable String date,@PathVariable String symbol) {
        return tradeRepo.findByActionAndDateAndSymbol(action,date,symbol);
    }

    @GetMapping("/trades/{action}/{status}/{symbol}")
    public List<Trade> getAllvalidorder(@PathVariable String action,@PathVariable String status,@PathVariable String symbol) {
        return tradeRepo.findByActionAndStatusAndSymbol(action,status, symbol);
    }

    

    
    @PostMapping("/buy/{acc_id}")
    public void BuyGenerate(@PathVariable Integer acc_id,@Valid @RequestBody Trade trade){
        //to check is the quantity is multiple of 100
        if(!checkQuantity(trade.getQuantity())){
            System.out.println("Input quantity is not valid");
            return;
        }
        // tradeRepo.save(trade);
    }


    @PostMapping("/sell/{acc_id}")
    public void SellGenerate(@PathVariable Integer acc_id,@Valid @RequestBody Trade trade){
        //to check is the quantity is multiple of 100
        if(!checkQuantity(trade.getQuantity())){
            System.out.println("Input quantity is not valid");
            return;
        }

        //tradeRepo.save(trade);
    }
    
    public void matching(Trade trade, Integer acc_id){
        // String date = trade.getDate().substring(0, 10);
        // List<Trade> orders = trades.getAllmatchingorder(trade.getAction(),date,trade.getSymbol());

        // double max = 0;
        // for(Trade t: orders){
        //     if(t.getBid() >= trade.getAsk() && t.getBid() > max){
        //         max = t.getBid();
        //     }
        // }
        // List<Trade> mList = tradeRepo.findBySymbolAndBid(trade.getSymbol(), max);
        // LocalDateTime edate = LocalDateTime.now();

        // for(Trade oinfo: mList){
        //     if(LocalDateTime.parse(oinfo.getDate()).compareTo(edate) < 0){
        //         edate = LocalDateTime.parse(oinfo.getDate());
        //     }
        // }

        List<Trade> bTrades = tradeRepo.findByActionAndStatusAndSymbol("buy","open",trade.getSymbol());
        List<Trade> bTrades2 = tradeRepo.findByActionAndStatusAndSymbol("buy","partial-filled",trade.getSymbol());
        bTrades.addAll(bTrades2);
        Collections.sort(bTrades);
        Collections.reverse(bTrades);   //descending order


        List<Trade> sTrades = tradeRepo.findByActionAndStatusAndSymbol("sell","open", trade.getSymbol());
        List<Trade> sTrades2 = tradeRepo.findByActionAndStatusAndSymbol("sell","partial-filled", trade.getSymbol());
        sTrades.addAll(sTrades2);
        Collections.sort(sTrades);  //ascending order
        
        
        Stock currstock = stockRepo.findBySymbol(trade.getSymbol());
        double currAsk = currstock.getAsk();
        double currBid = currstock.getBid();

        // *   + Buy trades having limit price above market price (current ask) will be matched at current ask.
        // *      * Example: a buy trade for A17U with price of $4 will be matched at $3.29 (current ask)
        // * 
        
        Boolean fill = false;
        int tradequantity = trade.getQuantity() - trade.getFilled_quantity();
        int i = 0;
        //for buying matching
        if(trade.getSymbol().equals("buy")){
            while(!fill){
                Trade s = sTrades.get(i);       //ascending order
                int squantity = s.getQuantity() - s.getFilled_quantity();   //available selling quantity
                // *   + Sell trades having limit price below market price (current bid) will be matched at current bid.
                // *   Example: a sell trade for A17U with price of $3 will be match at $3.26 (current bid)
                double askPrice = s.getAsk();
                double matchedPrice = askPrice;
                if(askPrice < currBid){         //not sure
                    matchedPrice = currBid;
                }

                // if selling quantity equal to buying quantity
                String tradeStatus = null;
                String sStatus = null;
                double total_price = squantity * matchedPrice;
                int sFilled_quantity = 0;
                int tradeFilled_quantity = 0;
                if(squantity < tradequantity){  //partially filled in buy trade, but sell trade -> "filled"
                    sStatus = "filled";
                    tradeStatus = "partial-filled";
                    sFilled_quantity = s.getQuantity();
                    tradeFilled_quantity = trade.getFilled_quantity() + squantity;
                    i++;
                    if(i == sTrades.size()){
                        fill = true;        //end the loop, but not filled
                    }
                } else if(squantity > tradequantity){//partially filled in sell trade, but buy trade -> "filled"
                    sStatus = "partial-filled";
                    tradeStatus = "filled";
                    sFilled_quantity = s.getFilled_quantity() + squantity;
                    tradeFilled_quantity = trade.getQuantity();
                    fill = true;
                } else if(squantity == tradequantity){
                    sStatus = "filled";
                    tradeStatus = "filled";
                    sFilled_quantity = s.getQuantity();
                    tradeFilled_quantity = trade.getQuantity();
                    fill = true;
                }

                //Make transaction 
                try{
                    Trans trans = new Trans(acc_id, s.getAccount_id(), total_price);  //from, to , ammount
                    Trans makeTrans = accController.makeTransaction(trans);

                    s.setFilled_quantity(s.getQuantity());
                    s.setStatus(sStatus);
                    trade.setFilled_quantity(trade.getFilled_quantity() + squantity);
                    trade.setStatus(tradeStatus);
                } catch (AccountNotFoundException e){
                    System.out.println(e.toString());
                } catch(ExceedAvailableBalanceException e){
                    System.out.println(e.toString());
                }
            }
            
        }else if(trade.getSymbol().equals("sell")){
            while(!fill){
                Trade b = bTrades.get(i);       //descending order
                int bquantity = b.getQuantity() - b.getFilled_quantity();   //available selling quantity
                //*   + Buy trades having limit price above market price (current ask) will be matched at current ask.
    //*      * Example: a buy trade for A17U with price of $4 will be matched at $3.29 (current ask)
                double bidPrice = b.getBid();
                double matchedPrice = bidPrice;
                if(bidPrice > currAsk){         //not sure
                    matchedPrice = currAsk;
                }

                // if selling quantity equal to buying quantity
                String tradeStatus = null;
                String bStatus = null;
                double total_price = bquantity * matchedPrice;
                int bFilled_quantity = 0;
                int tradeFilled_quantity = 0;
                if(bquantity < tradequantity){  //partially filled in sell trade, but buy trade -> "filled"
                    bStatus = "filled";
                    tradeStatus = "partial-filled";
                    bFilled_quantity = b.getQuantity();
                    tradeFilled_quantity = trade.getFilled_quantity() + bquantity;
                    i++;
                    if(i == bTrades.size()){
                        fill = true;        //end the loop, but not filled
                    }
                } else if(bquantity > tradequantity){//partially filled in buy trade, but sell trade -> "filled"
                    bStatus = "partial-filled";
                    tradeStatus = "filled";
                    bFilled_quantity = b.getFilled_quantity() + bquantity;
                    tradeFilled_quantity = trade.getQuantity();
                    fill = true;
                } else if(bquantity == tradequantity){
                    bStatus = "filled";
                    tradeStatus = "filled";
                    bFilled_quantity = b.getQuantity();
                    tradeFilled_quantity = trade.getQuantity();
                    fill = true;
                }

                //Make transaction 
                try{
                    Trans trans = new Trans(acc_id, b.getAccount_id(), total_price);  //from, to , ammount
                    Trans makeTrans = accController.makeTransaction(trans);

                    b.setFilled_quantity(b.getQuantity());
                    b.setStatus(bStatus);
                    trade.setFilled_quantity(trade.getFilled_quantity() + bquantity);
                    trade.setStatus(tradeStatus);
                } catch (AccountNotFoundException e){
                    System.out.println(e.toString());
                } catch(ExceedAvailableBalanceException e){
                    System.out.println(e.toString());
                }
        }
    }
    
    } 

    // //check the customer have enough balance for trading (buying)
    // public boolean checkBalance(Integer acc_id, double total_price){
    //     Optional<Account> acc = accRepo.findById(acc_id);
    //     Account account = acc.get();
    //     double availbalance = account.getAvailable_balance();
    //     if(availbalance >= total_bid_price){
    //         return true;
    //     }
    //     return false;
    // }

    //check the input quantity is multiple of 100
    public boolean checkQuantity(int quantity){
        if(quantity % 100 == 0){
            return true;
        }
        return false;
    }
}

