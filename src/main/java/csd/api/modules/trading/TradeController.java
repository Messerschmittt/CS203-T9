package csd.api.modules.trading;

import csd.api.tables.*;
import csd.api.tables.templates.*;

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


import csd.api.modules.account.*;
import csd.api.modules.trading.*;

@RestController
public class TradeController {
    private TradeRepository tradeRepo;
    private AccountRepository accRepo;
    private PortfolioRepository portfolioRepo;
    private AssestsRepository assestsRepo;
    private CustomerRepository custRepo;

    private AccountController accController;
    private StockController stockController;

    public TradeController(TradeRepository tradeRepo, AccountRepository accRepo, StockController stockController,
    PortfolioRepository portfolioRepo, AssestsRepository assetsRepo, CustomerRepository custRepo, AccountController accController){
        this.tradeRepo = tradeRepo;
        this.accRepo = accRepo;
        this.portfolioRepo = portfolioRepo;
        this.assestsRepo = assetsRepo;
        this.custRepo = custRepo;
        this.accController = accController;
        this.stockController = stockController;
    }

     /**
     * List all trades in the system
     * @return list of all trades
     */
    @GetMapping("/trades")
    public List<Trade> getTrade(){
        return tradeRepo.findAll();
    }

    //for ROLE_USER
    /**
     * Search for trade with the given id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     * @return trade with the given id
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

    

    
    @PostMapping("/trade/createTrade")
    public void TradeGenerate(@RequestBody TradeRecord tradeRecord){
        Account cusAcc = accRepo.findById(tradeRecord.getAccount_id()).get();
        Trade trade = new Trade(tradeRecord.getAction(), tradeRecord.getSymbol(), tradeRecord.getQuantity(), tradeRecord.getBid(), tradeRecord.getAsk(), 
        tradeRecord.getAvg_price(), tradeRecord.getFilled_quantity(), tradeRecord.getDate(), tradeRecord.getStatus(),  cusAcc);
        
        //to check is the quantity is multiple of 100
        if(!checkQuantity(trade.getQuantity())){
            System.out.println("Input quantity is not valid");
            return;
        }

        // check that customer has sufficient balance
        // U only need sufficient balance to buy not to sell
        if(trade.getAction().equals("buy")){
            if(trade.getBid() * trade.getQuantity() > cusAcc.getAvailable_balance()){
                throw new InsufficientBalanceForTradeException(trade.getId());
            }
        }


        // Enter Create and Matching Function
        matching(trade);
          
    }

    //find the matching trade    
    public void matching(Trade newTrade){

        List<Trade> bTrades = tradeRepo.findByActionAndStatusAndSymbol("buy","open",newTrade.getSymbol());
        List<Trade> bTrades2 = tradeRepo.findByActionAndStatusAndSymbol("buy","partial-filled",newTrade.getSymbol());
        bTrades.addAll(bTrades2);
        Collections.sort(bTrades);
        Collections.reverse(bTrades);   //descending order


        List<Trade> sTrades = tradeRepo.findByActionAndStatusAndSymbol("sell","open", newTrade.getSymbol());
        List<Trade> sTrades2 = tradeRepo.findByActionAndStatusAndSymbol("sell","partial-filled", newTrade.getSymbol());
        sTrades.addAll(sTrades2);
        Collections.sort(sTrades);  //ascending order
        
        Boolean tradeNotFilled = true;
        int i = 0;
        int initialTradeQty = newTrade.getQuantity() - newTrade.getFilled_quantity();
        double lastPrice = 0.0;

        if(newTrade.getAction().equals("buy")){
            double newTradeBid = newTrade.getBid();
            int tradeFilledQuantity = 0;
            int currentTradeQty = initialTradeQty;
            while(tradeNotFilled && i < sTrades.size()){
                Trade s = sTrades.get(i);
                double sAskPrice = s.getAsk();

                if(newTradeBid == 0){ // check if market order
                    // skip the price checking
                }else if(newTradeBid < sAskPrice){ // once there are no more ask orders below bid --> save & return
                    tradeRepo.save(newTrade);
                    return;
                }

                String newTradeStatus = null;
                String sStatus = null;
                double transaction_amt = 0.0;
                int transaction_quantity = 0;
                int sAvailQuantity = s.getQuantity() - s.getFilled_quantity();   //available selling quantity
                int sFilledQuantity = s.getFilled_quantity();

                if(sAvailQuantity < currentTradeQty){  //partially filled in buy trade, but sell trade -> "filled"
                    sStatus = "filled";
                    newTradeStatus = "partial-filled";
                    transaction_quantity = sAvailQuantity;

                } else if(sAvailQuantity > currentTradeQty){  //partially filled in sell trade, but buy trade -> "filled"
                    sStatus = "partial-filled";
                    newTradeStatus = "filled";
                    transaction_quantity = currentTradeQty;
    
                } else if(sAvailQuantity == currentTradeQty){
                    sStatus = "filled";
                    newTradeStatus = "filled";
                    transaction_quantity = currentTradeQty;

                }
                System.out.println("i:  " + i);
                i++;
                tradeFilledQuantity += transaction_quantity;
                currentTradeQty -= transaction_quantity;
                transaction_amt = transaction_quantity * sAskPrice;
                lastPrice = sAskPrice;
                

                Account cusAcc = newTrade.getAccount();
                System.out.println("customerAcc: " + cusAcc.getId());
                // Create transaction between buyer and seller
                Trans t = new Trans();
                t.setAmount(transaction_amt);
                t.setFrom_account(cusAcc);
                t.setTo_account(s.getAccount());
                if(s.getAccount() == null){
                    System.out.println("Faulty account accessed");
                    t.setTo_account(new Account());
                }
                accController.makeTransaction(t);
                
                // Only save when transaction is successful ie. theres sufficient funds
                // Update s trade status
                sFilledQuantity += transaction_quantity;
                s.setStatus(sStatus);
                s.setFilled_quantity(sFilledQuantity);

                // Update newTrade trade status
                newTrade.setStatus(newTradeStatus);
                newTrade.setFilled_quantity(tradeFilledQuantity);

                // Check if new trade is filled
                if(tradeFilledQuantity == initialTradeQty){
                    tradeNotFilled = false;
                }

                // Save trades
                tradeRepo.save(s);
                tradeRepo.save(newTrade);
                
                // Create/Update asset record
                Customer c = newTrade.getAccount().getCustomer();
                Assests a = assestsRepo.findByCustomer_IdAndCode(c.getId(), newTrade.getSymbol());
                if(a == null){
                    a = new Assests();
                    a.setCode(newTrade.getSymbol());
                    a.setCustomer(c);
                    a.setAvg_price(sAskPrice);
                    a.setQuantity(a.getQuantity() + transaction_quantity);
                }else{
                    int priorQuantity = a.getQuantity();
                    double priorAvgPrice = a.getAvg_price();
                    double newAvgPrice = ((priorQuantity * priorAvgPrice) + (transaction_quantity * transaction_amt))/(transaction_quantity + priorQuantity);

                    a.setAvg_price(newAvgPrice);
                    a.setQuantity(priorQuantity + transaction_quantity);
                }

                assestsRepo.save(a);
            }

        }
        
        if(newTrade.getAction().equals("sell")){
            System.out.println("In sell");
            double newTradeAsk = newTrade.getAsk();
            int tradeFilledQuantity = 0;
            int currentTradeQty = initialTradeQty;
            while(tradeNotFilled && i < bTrades.size()){
                Trade b = bTrades.get(i);
                double bBidPrice = b.getBid();

                if(newTradeAsk == 0){
                    // skip the price checking
                }else if(newTradeAsk > bBidPrice){ // once there are no more bid orders above the ask --> save & return
                    tradeRepo.save(newTrade);
                    return;
                }

                String newTradeStatus = null;
                String bStatus = null;
                double transaction_amt = 0.0;
                int transaction_quantity = 0;
                int bAvailQuantity = b.getQuantity() - b.getFilled_quantity();   //available selling quantity
                int bFilledQuantity = b.getFilled_quantity();

                if(bAvailQuantity < currentTradeQty){  //partially filled in buy trade, but sell trade -> "filled"
                    bStatus = "filled";
                    newTradeStatus = "partial-filled";
                    transaction_quantity = bAvailQuantity;

                } else if(bAvailQuantity > currentTradeQty){  //partially filled in sell trade, but buy trade -> "filled"
                    bStatus = "partial-filled";
                    newTradeStatus = "filled";
                    transaction_quantity = currentTradeQty;
    
                } else if(bAvailQuantity == currentTradeQty){
                    bStatus = "filled";
                    newTradeStatus = "filled";
                    transaction_quantity = currentTradeQty;

                }
                System.out.println("i:  " + i);
                i++;
                tradeFilledQuantity += transaction_quantity;
                currentTradeQty -= transaction_quantity;
                transaction_amt = transaction_quantity * bBidPrice;
                lastPrice = bBidPrice;

                Account cusAcc = newTrade.getAccount();
                System.out.println("customerAcc: " + cusAcc.getId());
                // Create transaction between buyer and seller
                Trans t = new Trans();
                t.setAmount(transaction_amt);
                t.setFrom_account(b.getAccount());
                t.setTo_account(cusAcc);
                if(b.getAccount() == null){
                    System.out.println("Bank account accessed");
                    t.setFrom_account(new Account(null, 1_000_000, 1_000_000)); //using an arbitary value for bank
                }
                accController.makeTransaction(t);
                
                // Update s trade status
                bFilledQuantity += transaction_quantity;
                b.setStatus(bStatus);
                b.setFilled_quantity(bFilledQuantity);

                // Update newTrade trade status
                newTrade.setStatus(newTradeStatus);
                newTrade.setFilled_quantity(tradeFilledQuantity);

                // Check if new trade is filled
                if(tradeFilledQuantity == initialTradeQty){
                    tradeNotFilled = false;
                }

                // Save trades
                tradeRepo.save(b);
                tradeRepo.save(newTrade);
                
                // Create/Update asset record
                Customer c = newTrade.getAccount().getCustomer();
                Assests a = assestsRepo.findByCustomer_IdAndCode(c.getId(), newTrade.getSymbol());
                if(a == null){
                   System.out.println("asset not found");
                   // Shld throw a real exception
                }else{
                    int priorQuantity = a.getQuantity();
                    double priorAvgPrice = a.getAvg_price();
                    int newQuantity = priorQuantity - transaction_quantity;
                    System.out.println("after sell quantity" + newQuantity);
                    double newAvgPrice = 0;
                    if(newQuantity != 0){
                        newAvgPrice = ((priorQuantity * priorAvgPrice) - (transaction_quantity * transaction_amt))/(priorQuantity - transaction_quantity);
                    }

                    a.setAvg_price(newAvgPrice);
                    a.setQuantity(priorQuantity - transaction_quantity);
                }

                assestsRepo.save(a);
            }
        }

        stockController.refreshStockPrice(newTrade.getSymbol(), lastPrice);
    } 


    //check the input quantity is multiple of 100
    private boolean checkQuantity(int quantity){
        if(quantity % 100 == 0){
            return true;
        }
        return false;
    }
}
