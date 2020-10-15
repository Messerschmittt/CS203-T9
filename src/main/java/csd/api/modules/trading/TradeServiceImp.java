package csd.api.modules.trading;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import csd.api.tables.*;
import csd.api.tables.templates.*;
import csd.api.modules.user.*;
import csd.api.modules.account.*;
import csd.api.modules.trading.*;

@Service
public class TradeServiceImp implements TradeService {
    private TradeRepository tradeRepo;
    private AccountRepository accRepo;
    private PortfolioRepository portfolioRepo;
    private AssetsRepository assetsRepo;
    private CustomerRepository custRepo;
    private StockRepository stockRepo;

    private AccountController accController;
    private StockController stockController;

    /**
     * Constructor of TradeServiceImp implementing TradeService
     * @param tradeRepo
     * @param accRepo
     * @param portfolioRepo
     * @param assetsRepo
     * @param custRepo
     * @param stockRepo
     * @param accController
     * @param stockController
     */
    public TradeServiceImp(TradeRepository tradeRepo, AccountRepository accRepo, 
            PortfolioRepository portfolioRepo, AssetsRepository assetsRepo, CustomerRepository custRepo, 
            StockRepository stockRepo, AccountController accController, StockController stockController) {
        this.tradeRepo = tradeRepo;
        this.accRepo = accRepo;
        this.portfolioRepo = portfolioRepo;
        this.assetsRepo = assetsRepo;
        this.custRepo = custRepo;
        this.stockRepo = stockRepo;
        this.accController = accController;
        this.stockController = stockController;
    }

     /**
     * List all trades in the system
     * @return list of all trades
     */
    @Override
    public List<Trade> listTrades(){
        return tradeRepo.findAll();
    }

    //for ROLE_USER
    /**
     * Search for trade with the given id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     * @return trade with the given id
     */
    @Override
    public Trade getTrade(Integer id) {
        return tradeRepo.findById(id).orElse(null);
    }

    /**
     * Remove the specific trade record
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     */
    @Override
    public void deleteTrade(Integer id){
        if(!tradeRepo.existsById(id)) {
            throw new TradeNotFoundException(id);
        }
        tradeRepo.deleteById(id);
    }
    
    //check is the stock symbol valid
    @Override
    public boolean checkSymbol(String symbol){
        //check symbol 
        boolean isValid = true;
        if(stockRepo.findBySymbol(symbol) == null){
            // throw new InvalidInputException(inputsymbol, "Stock Symbol");
            isValid = false;
        }
        return isValid;
    }

    //check is stock market open. Market open on Weekdays 9am tp 5pm.
    @Override
    public boolean checkTime(){
        boolean isValid = false;
        LocalDateTime now = LocalDateTime.now();
        int nowtime = now.getHour();
        DayOfWeek day = now.getDayOfWeek();
        switch (day) {
            case SATURDAY:
                throw new InvalidTradeTiming();
            case SUNDAY:
                throw new InvalidTradeTiming();
            default:
                if(nowtime < 9 || nowtime >= 17){
                    List<Trade> invalidtrades = tradeRepo.findByStatusContainingOrStatusContaining("open", "partial-filled");
                    for(Trade t: invalidtrades){
                        t.setStatus("expired"); //update volumn in stock??
                        tradeRepo.save(t);
                    }
                } else{
                    isValid = true;
                }
        }
        return isValid;
    }

    //check the input quantity is multiple of 100
    @Override
    public boolean checkQuantity(int quantity){
        if(quantity % 100 == 0){
            return true;
        }
        return false;
    }

    //delete after all done
    //Test Sorting -- //--can change to normal function (no need mapping)
    // @GetMapping("/trade/SortSellTrade/{action}/{symbol}")
    // public List<Trade> Sort(@PathVariable String action,@PathVariable String symbol){

    //sorting all the open or partial-filled sellTrades with specific stock symbol
    @Override
    public List<Trade> sellTradesSorting(String symbol){
        Sort sSort = Sort.by("ask").ascending().and(Sort.by("date").ascending());
        List<Trade> sTrades = tradeRepo.findByActionAndSymbolAndStatusContainingOrStatusContaining("sell", symbol, "open", "partial", sSort);
        return sTrades;
    }

    //sorting all the open or partial-filled buyTrades with specific stock symbol
    @Override
    public List<Trade> buyTradesSorting(String symbol){
        Sort bSort = Sort.by("bid").descending().and(Sort.by("date").ascending());
        List<Trade> bTrades = tradeRepo.findByActionAndSymbolAndStatusContainingOrStatusContaining("buy", symbol, "open", "partial", bSort);
        return bTrades;
    }

    //find the matching trade   
    @Override    
    public Trade matching(Trade newTrade){
        List<Trade> sTrades = sellTradesSorting(newTrade.getSymbol());  //sorted list of sellTrades
        List<Trade> bTrades = buyTradesSorting(newTrade.getSymbol());   //sorted list of buyTrades

        Boolean tradeNotFilled = true;
        int i = 0;
        int initialTradeQty = newTrade.getQuantity() - newTrade.getFilled_quantity();
        Account cusAcc = newTrade.getAccount();
        Customer customer = cusAcc.getCustomer();
        Portfolio cusPortfolio = customer.getPortfolio();
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
                    return newTrade;
                }

                String newTradeStatus = null;       //for update the status of newTrade
                String sStatus = null;              //for update the status of sTrade
                double transaction_amt = 0.0;
                int transaction_quantity = 0;
                int sAvailQuantity = s.getQuantity() - s.getFilled_quantity();   //available selling quantity
                int sFilledQuantity = s.getFilled_quantity();
                double gain_loss = 0.0;     //to update in Portfolio
                
                if(sAvailQuantity < currentTradeQty){  //partially filled in newTrade(buy), but sell trade -> "filled"
                    sStatus = "filled";
                    newTradeStatus = "partial-filled";
                    transaction_quantity = sAvailQuantity;

                } else if(sAvailQuantity > currentTradeQty){  //partially filled in sell trade, but newTrade(buy) -> "filled"
                    sStatus = "partial-filled";
                    newTradeStatus = "filled";
                    transaction_quantity = currentTradeQty;
    
                } else if(sAvailQuantity == currentTradeQty){   //both trades status "filled"
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

                System.out.println("customerAcc: " + cusAcc.getId());
                
                // Create transaction between buyer and seller
                Trans t = new Trans();
                t.setAmount(transaction_amt);
                t.setFrom_account(cusAcc);
                t.setTo_account(s.getAccount());
                //---?
                if(s.getAccount() == null){
                    System.out.println("Faulty account accessed");
                    t.setTo_account(new Account());
                }
                tradeRepo.save(newTrade);
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

                //update the stock buy volume and price
                Stock targetstock = stockRepo.findBySymbol(newTrade.getSymbol());
                if(newTrade.getBid() > targetstock.getBid()){
                    targetstock.setBid(newTrade.getBid());
                    targetstock.setBid_volume(tradeFilledQuantity); 
                }else if(newTrade.getBid() == targetstock.getBid()){
                    targetstock.setBid_volume(targetstock.getBid_volume() + tradeFilledQuantity);
                }
                stockRepo.save(targetstock);
                
                // Create/Update asset record
                Assets a = assetsRepo.findByCustomer_IdAndCode(customer.getId(), newTrade.getSymbol());
                
                if(a == null){
                    a = new Assets();
                    a.setCode(newTrade.getSymbol());
                    a.setCustomer(customer);
                    a.setAvg_price(sAskPrice);
                    a.setQuantity(a.getQuantity() + transaction_quantity);
                }else{
                    int priorQuantity = a.getQuantity();
                    double priorAvgPrice = a.getAvg_price();
                    double newAvgPrice = ((priorQuantity * priorAvgPrice) + transaction_amt)/(transaction_quantity + priorQuantity);
                    a.setAvg_price(newAvgPrice);
                    a.setQuantity(priorQuantity + transaction_quantity);
                }
                a.setCurrent_price(lastPrice);          //should keep updating for every trade???
                a.CalculateValue();
                a.CalculateGain_loss();
                assetsRepo.save(a);

                //need to check again ---
                gain_loss = (a.getCurrent_price() - a.getAvg_price()) * transaction_amt;
                cusPortfolio.updateTotal_gain_loss(gain_loss); 
                cusPortfolio.updateUnrealised();
                portfolioRepo.save(cusPortfolio);

                stockController.refreshStockPrice(newTrade.getSymbol(), lastPrice);
            }
        }
        
        //for sell action
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
                    return newTrade;
                }

                String newTradeStatus = null;
                String bStatus = null;
                double transaction_amt = 0.0;
                int transaction_quantity = 0;
                int bAvailQuantity = b.getQuantity() - b.getFilled_quantity();   //available selling quantity
                int bFilledQuantity = b.getFilled_quantity();
                double gain_loss = 0.0;     //to update in Portfolio

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
                tradeRepo.save(newTrade);
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

                //update stock sell volumn and price
                 Stock targetstock = stockRepo.findBySymbol(newTrade.getSymbol());
                 if(newTrade.getAsk() > targetstock.getAsk()){
                     targetstock.setAsk(newTrade.getAsk());
                     targetstock.setAsk_volume(tradeFilledQuantity); 
                 }else if(newTrade.getAsk() == targetstock.getAsk()){
                     targetstock.setAsk_volume(targetstock.getAsk_volume() + tradeFilledQuantity);
                 }
                 stockRepo.save(targetstock);
                
                // Create/Update asset record
                Assets a = assetsRepo.findByCustomer_IdAndCode(customer.getId(), newTrade.getSymbol());
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
                        newAvgPrice = ((priorQuantity * priorAvgPrice) - (transaction_amt))/(priorQuantity - transaction_quantity);
                    }

                    a.setAvg_price(newAvgPrice);
                    a.setQuantity(priorQuantity - transaction_quantity);
                }

                a.setCurrent_price(lastPrice);
                a.CalculateValue();
                a.CalculateGain_loss();
                assetsRepo.save(a);

                //need to check again ---
                gain_loss = (a.getCurrent_price() - a.getAvg_price()) * transaction_amt;
                cusPortfolio.updateTotal_gain_loss(gain_loss); 
                cusPortfolio.updateUnrealised();
                portfolioRepo.save(cusPortfolio);

                stockController.refreshStockPrice(newTrade.getSymbol(), lastPrice);
            }
        }
        return newTrade;
    } 
    
    /** --------------need modify
     * Check if the user input valid stock symbol, and is the stock market 
     * then create trade and perform trade matching
     * @param tradeRecord
     * @return the latest trade info
     */
    @Override
    public Trade TradeGenerate(TradeRecord tradeRecord){
        
        boolean isValidSymbol = checkSymbol(tradeRecord.getSymbol());
        boolean isValidTime = checkTime();
        boolean isValidQty = checkQuantity(tradeRecord.getQuantity());

        //if stock symbol is invalid, throw InvalidInputException
        if(!isValidSymbol){
            throw new InvalidInputException(tradeRecord.getSymbol(), "Stock Symbol ");
        }

        //if stock market is close, throw InvalidTradeTiming
        if(!isValidTime) {
            throw new InvalidTradeTiming();
        }

        //if the quantity is not multiple of 100, throw InvalidInputException
        String qty = "" + tradeRecord.getQuantity();
        if(!isValidQty){
            throw new InvalidInputException(qty, "Input quantity. It should be multiiple of 100");
        }

        Account cusAcc = accRepo.findById(tradeRecord.getAccount_id()).get();
        Trade trade = new Trade(tradeRecord.getAction(), tradeRecord.getSymbol(), tradeRecord.getQuantity(), tradeRecord.getBid(), tradeRecord.getAsk(), 
        tradeRecord.getAvg_price(), tradeRecord.getFilled_quantity(), tradeRecord.getDate(), tradeRecord.getStatus(),  cusAcc);


        // do we need to update in available balance??
        // check that customer has sufficient balance
        // U only need sufficient balance to buy not to sell
        if(trade.getAction().equals("buy")){
            if(trade.getBid() * trade.getQuantity() > cusAcc.getAvailable_balance()){
                throw new InsufficientBalanceForTradeException(trade.getId());
            }
        }
        
        // Enter Create and Matching Function (return the latest trade info)
        return matching(trade);
    }

}

