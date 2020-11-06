package csd.api.modules.trading;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import csd.api.tables.*;
import csd.api.tables.templates.*;
import csd.api.modules.user.*;
import csd.api.modules.account.*;
import csd.api.modules.trading.*;
import csd.api.modules.portfolio.*;
import static csd.api.modules.account.RyverBankAccountConstants.*;

@Service
public class TradeServiceImp implements TradeService {
    private TradeRepository tradeRepo;
    private AccountRepository accRepo;
    private PortfolioRepository portfolioRepo;
    private AssetsRepository assetsRepo;
    private StockRepository stockRepo;
    private AccountController accController;
    private StockController stockController;
    private CustomerRepository cusRepo;

    /**
     * Constructor of TradeServiceImp implementing TradeService
     * @param tradeRepo
     * @param accRepo
     * @param portfolioRepo
     * @param assetsRepo
     * @param stockRepo
     * @param accController
     * @param stockController
     */
    public TradeServiceImp(TradeRepository tradeRepo, AccountRepository accRepo, 
            PortfolioRepository portfolioRepo, AssetsRepository assetsRepo,
            StockRepository stockRepo, AccountController accController, StockController stockController, CustomerRepository cusRepo) {
        this.tradeRepo = tradeRepo;
        this.accRepo = accRepo;
        this.portfolioRepo = portfolioRepo;
        this.assetsRepo = assetsRepo;
        this.stockRepo = stockRepo;
        this.accController = accController;
        this.stockController = stockController;
        this.cusRepo = cusRepo;
    }

    /**
     * List all the trades in the trade repository
     */
    @Override
    public List<Trade> getAllTrades(){
        return tradeRepo.findAll();
    }

    /**
     * Search for trade with the given id
     * If there is no trade with the given "id", return null
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

    /**
     * cancel the specific trade record using trade id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     */
    @Override
    public Trade CancelTrade(Integer id){
        System.out.println("Cancelling Trade");
        Trade trade = tradeRepo.findById(id).get();
        String tradestatus =  trade.getStatus();
        // if trade status is open -> set it expired
        if(tradestatus.equals("open")){
            trade.setStatus("cancelled");
            tradeRepo.save(trade);
        }

        ///need to check again ---------
        Account cusAcc = trade.getAccount();
        if(trade.getBid() == 0){
            double marketAsk = stockRepo.findBySymbol(trade.getSymbol()).getAsk();
            double priceCheck = marketAsk * (trade.getQuantity()-trade.getFilled_quantity());
            cusAcc.setAvailable_balance(cusAcc.getAvailable_balance()+priceCheck);
        }else{
            cusAcc.setAvailable_balance(cusAcc.getAvailable_balance()+(trade.getBid()*trade.getQuantity()));
        }
        accRepo.save(cusAcc);   
        return trade;
    }

    /**
     * Check the validation of input stock symbol
     * If stock symbol is invalid, throw InvalidInputException
     * @param symbol
     */
    @Override
    public void checkSymbol(String symbol){
        if(stockRepo.findBySymbol(symbol) == null){
            throw new InvalidInputException(symbol, "Stock Symbol ");
        }
    }
    
    /**
     * Check the validation of input quantity. Quantity should be multiple of 100 (and not negative)
     * If quantity is not multiple of 100 or is negative number, throw InvalidInputException (400)
     * @param quantity
     */
    @Override
    public void checkQuantity(int quantity){
        if(quantity % 100 != 0 || quantity < 0){
            throw new InvalidInputException(""+ quantity, "input quantity. It should be multiiple of 100 and positive number");
        }
    }

    //check -------------------
    /**
     * Check does stock market open or not. Market open on Weekdays 9am tp 5pm.
     * If now is past 5pm of the day, expire all trades before 5pm that day
     * if its after 5pm return false
     * @return boolean value
     */
    @Override
    public boolean checkTime(){
        boolean isValid = false;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(8));
        // LocalDateTime now = LocalDateTime.parse("2020-11-02T11:44:44.797");
        String date = now.toString().substring(0, 11);      //for checking the date of trades are same or not
        //for checking the market open time
        int nowtime = now.getHour();
        DayOfWeek day = now.getDayOfWeek();
        switch (day) {
            case SATURDAY:
                return isValid;
            case SUNDAY:
                return isValid;
            default:
                if(nowtime < 9){
                    return isValid;
                } else if( nowtime >= 17){
                    //check if the date of open or partial-filled trades is same with the date of current trade
                    List<Trade> trades = tradeRepo.findByStatusContainingOrStatusContaining("open", "partial-filled");
                    if(trades != null && !trades.isEmpty()){
                        updateStatusToExpired();
                    }
                }
                else{     //if stock market open
                    isValid = true;
                }
        }
        return isValid;
    }

    //check the quantity of stock the customer can buy based on available balance
    @Override
    public boolean checkBalance(Trade trade, Account cusAcc){
        boolean valid = true;
        // double marketPrice = stockRepo.findBySymbol(trade.getSymbol()).getBid();
        // List<Trade> allSellTradesForStock = tradeRepo.findBySymbol(trade.getSymbol());
        // allSellTradesForStock.removeIf(t -> t.getAsk() == 0.0);
        // double marketCap = 0;
        // for(Trade t : allSellTradesForStock){
        //     marketCap += (t.getQuantity() - t.getFilled_quantity())*t.getAsk();
        // }

        double available = cusAcc.getAvailable_balance();
        if(trade.getBid() == 0){
            double marketAsk= stockRepo.findBySymbol(trade.getSymbol()).getAsk();
            double priceCheck = marketAsk * (trade.getQuantity()-trade.getFilled_quantity());
            if(cusAcc.getAvailable_balance() < priceCheck){
                return false;
            }
            cusAcc.setAvailable_balance(cusAcc.getAvailable_balance() - priceCheck);
            accRepo.save(cusAcc);
        }
        if(trade.getBid() * trade.getQuantity() > available){
            return false;
        }
        // reduce avaialble balance of customer
        
        cusAcc.setAvailable_balance(cusAcc.getAvailable_balance() - trade.getBid() * trade.getQuantity());
        accRepo.save(cusAcc);
        return valid;
    }

    //during matching should be able to buy the stock quantity as much as possible
    @Override
    public int getMaxStock(int qty, double price, double availableBalance){        //qty want to buy, price of stock
        int max = qty;
        if(availableBalance < qty * price){
            max = (int) ((availableBalance / price) / 100) * 100;        //ensure is multiple of 100
            System.out.println("max: " + max + ", original: " + qty);
        }
        if(max == 0){
            throw new InsufficientBalanceForTradeException();
        }
        return max;
    }

    /**
     * Update the trades status of "open" and "partial-filled" as expired
     */
    @Override
    public void updateStatusToExpired(){
        //check openTrade time is between 9am and 5pm -> set it expired
        List<Trade> OpenPartialTrades = tradeRepo.findByStatusContainingOrStatusContaining("open", "partial");
        if(OpenPartialTrades == null || OpenPartialTrades.isEmpty()){
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(8));
        String day = now.toString().substring(0,9);

        for(Trade t: OpenPartialTrades){
            String time = t.getDate().substring(11,13);
            String t_day = t.getDate().substring(0, 9);
            int hr = Integer.parseInt(time);
            if(hr >= 9 && hr < 17 || !day.equals(t_day)){ // check for nt same day or trades done in trading hours of same day
                t.setStatus("expired");
                tradeRepo.save(t);

                Account cusAcc = t.getAccount();
                if(t.getBid() == 0){
                    double marketAsk = stockRepo.findBySymbol(t.getSymbol()).getAsk();
                    double priceCheck = marketAsk * (t.getQuantity()-t.getFilled_quantity());
                    cusAcc.setAvailable_balance(cusAcc.getAvailable_balance()+priceCheck);
                }else{
                    cusAcc.setAvailable_balance(cusAcc.getAvailable_balance()+(t.getBid()*(t.getQuantity()-t.getFilled_quantity())));
                }
                accRepo.save(cusAcc);
            }
        }
    }

    // /**
    //  * Check the validation of input bid and sell price. Should not be negative
    //  * @param price
    //  */
    // @Override
    // public void checkValidPrice(int price){
    //     if(price < 0){
    //         throw new InvalidInputException(""+ price, "input price. It should not be negative");
    //     }
    // }

    
    // check is the customer has enough stock to sell
    @Override 
    public void checkAssets(int customerID, String symbol, int qty){
        List<Trade> cusOpenTrade = tradeRepo.findByCustomer_IdAndSymbolAndStatus(customerID, symbol,"open");
        cusOpenTrade.removeIf(t -> (t.getAction().equals("buy")) );
        List<Trade> cusPartialTrade = tradeRepo.findByCustomer_IdAndSymbolAndStatus(customerID, symbol,"partial-filled");
        cusPartialTrade.removeIf(t -> (t.getAction().equals("buy")) );
        int selling = 0;
        
        for(Trade ot: cusOpenTrade){
            selling += ot.getQuantity();
        }
        for(Trade pt: cusPartialTrade){
            selling += pt.getQuantity() - pt.getFilled_quantity();
        }

        //check if the user have the stock
        Assets assets = assetsRepo.findByCustomer_IdAndCode(customerID, symbol);
        
        //if not found the stock in assets list, throw AssetsNotFoundException with stock symbol
        if(assets == null){
            throw new AssetsNotFoundException(symbol);
        }

        //if there is stock in assets, check if the quantity is enough, if not throw InsufficientStockException
        int available = assets.getQuantity() - selling;
        if(qty > available){
            throw new InsufficientStockException();
        }
    }

    /**
     * Sort all the open or partial-filled of specific stock sell trades in ascending order
     * of ask price and date.
     * @param symbol
     * @return list of sorted sell Trades
     */
    @Override
    public List<Trade> sellTradesSorting(String symbol){
        List<Trade> sTrades = tradeRepo.findByActionAndSymbolAndStatus("sell", symbol, "open");
        sTrades.addAll(tradeRepo.findByActionAndSymbolAndStatus("sell", symbol, "partial-filled"));
        sTrades.sort((t1,t2) -> {
            if(t1.getBid() == t2.getBid()){
                LocalDateTime t1_date = LocalDateTime.parse(t1.getDate());
                LocalDateTime t2_date = LocalDateTime.parse(t2.getDate());
                return t1_date.compareTo(t2_date);
            }else{
                return (t1.compareTo(t2));
            }
        });
        return sTrades;
    }

    /**
     * Sort all the open or partial-filled of specific stock buy trades in descending order
     * of bid price and ascending order of date.
     * @param symbol
     * @return list of sorted buy Trades
     */
    @Override
    public List<Trade> buyTradesSorting(String symbol){
        List<Trade> bTrades = tradeRepo.findByActionAndSymbolAndStatus("buy", symbol, "open");
        bTrades.addAll(tradeRepo.findByActionAndSymbolAndStatus("buy", symbol, "partial-filled"));
        bTrades.sort((t1,t2) -> {
            if(t1.getBid() == t2.getBid()){
                LocalDateTime t1_date = LocalDateTime.parse(t1.getDate());
                LocalDateTime t2_date = LocalDateTime.parse(t2.getDate());
                return t1_date.compareTo(t2_date);
            }else{
                return -(t1.compareTo(t2));
            }
        });
        return bTrades;
    }

    //--check ------------------------------
    //update both side of sell and buy average price
    public void updateAvg_Price(Trade trade, double price, double transaction_amt, int transaction_qty ){
        double avg_price = trade.getAvg_price();
        if(avg_price == 0){
            trade.setAvg_price(price);
        } else{
            trade.setAvg_price((avg_price * trade.getFilled_quantity() + transaction_amt) / (transaction_qty + trade.getFilled_quantity()));
        }
    }

    @Override
    public Assets updateBuyerAssets(Customer customer, Trade newTrade, int transaction_qty , 
                double transaction_amt, double currentPrice){
        // if its the bank, no need update assests
        if (customer.getId() == BANK_CUSTOMER.getId()) {
            return null;
        }
        // Create/Update asset record
        Assets a = assetsRepo.findByCustomer_IdAndCode(customer.getId(), newTrade.getSymbol());
        if (a == null) { // create new assets
            a = new Assets();
            a.setCode(newTrade.getSymbol());
            a.setCustomer(customer);
            a.setAvg_price(currentPrice);
            a.setQuantity(transaction_qty );
        } else { // update assets
            int priorQuantity = a.getQuantity();
            double priorAvgPrice = a.getAvg_price();
            double newAvgPrice = ((priorQuantity * priorAvgPrice) + transaction_amt)
                    / (transaction_qty + priorQuantity);
            a.setAvg_price(newAvgPrice);
            a.setQuantity(priorQuantity + transaction_qty );
        }
        a.setCurrent_price(currentPrice);
        a.CalculateValue();
        a.CalculateGain_loss();
        assetsRepo.save(a);

        //update Portfolio also
        Portfolio cusPortfolio = customer.getPortfolio();
        double gain_loss = (a.getCurrent_price() - a.getAvg_price()) * transaction_amt;
        a.setCustomer(customer);
        a.setPortfolio(cusPortfolio);
        cusPortfolio.updateTotal_gain_loss(gain_loss); 
        cusPortfolio.updateUnrealised();
        portfolioRepo.save(cusPortfolio);

        return a;
    }
    
    @Override
    public Assets updateSellerAssets(Customer customer, Trade newTrade, int transaction_qty , 
    double transaction_amt, double currentPrice){
        if(customer.getId() == BANK_CUSTOMER.getId()){
            return null;
        }
        // Create/Update asset record
        Assets a = assetsRepo.findByCustomer_IdAndCode(customer.getId(), newTrade.getSymbol());
        int priorQuantity = a.getQuantity();
        double priorAvgPrice = a.getAvg_price();
        int newQuantity = priorQuantity - transaction_qty ;
        
        a.setQuantity(newQuantity);
        a.setCurrent_price(currentPrice);
        a.CalculateValue();
        a.CalculateGain_loss();
        assetsRepo.save(a);

        //update Portfolio also
        Portfolio cusPortfolio = customer.getPortfolio();
        double gain_loss = (a.getCurrent_price() - a.getAvg_price()) * transaction_amt;
        a.setCustomer(customer);
        a.setPortfolio(cusPortfolio);
        cusPortfolio.updateTotal_gain_loss(gain_loss); 
        cusPortfolio.updateUnrealised();
        portfolioRepo.save(cusPortfolio);

        if(newQuantity == 0){       //remove assests from repo
            assetsRepo.deleteById(a.getId());
        }

        return a;
    }

    // During matching, compare buyqty and sellqty, update the status and transaction qt 
    public String[] update_Status_Qty(int maxBuy, int buyQty, int sellQty){
        //Trade buyTrade, Trade sellTrade
        String[] ans = new String[3];
        String sellStatus = null;
        String buyStatus = null;
        int transaction_qty = 0;

        if(maxBuy == buyQty){
            if( (sellQty < buyQty) ){  //"partial-filled" in buyTrade, sellTrade "filled"
                sellStatus = "filled";
                buyStatus = "partial-filled";
                transaction_qty = sellQty;
            } else if(sellQty > buyQty){  //"partial-filled" in sellTrade, buyTrade "filled"
                sellStatus = "partial-filled";
                buyStatus = "filled";
                transaction_qty = buyQty;
            } else if(sellQty == buyQty){   //both trades status "filled"
                sellStatus = "filled";
                buyStatus = "filled";
                transaction_qty = buyQty;
            } 
        } else{     //if (maxBuy < buyQty)
            if( sellQty <= maxBuy ){  //partially filled in buyTrade, sell trade "filled"
                sellStatus = "filled";
                buyStatus = "partial-filled";
                transaction_qty = sellQty;
            } else if(sellQty > maxBuy){  //partially filled in both trade
                sellStatus = "partial-filled";
                buyStatus = "partial-filled";
                transaction_qty = maxBuy;
            }
        }

        ans[0] = buyStatus;
        ans[1] = sellStatus;
        ans[2] = String.valueOf(transaction_qty);
        return ans;
    }

    // if no trade to be matched, save the trade and return false, else return true
    public boolean HaveMatchTrade(List<Trade> tradeList, Trade Trade, double price){
        if(tradeList == null || tradeList.size() == 0){     //if no matching trade
            tradeRepo.save(Trade);
            stockController.refreshStockPrice(Trade.getSymbol(), price);  ///------?need to check
            return false;
        }
        return true;
    }

    // check would the trade is able to match. 
    // If it is not able to match -> save the trade and return it, else return null (able to match)
    public Trade checkHasMatching(double buyBid, double sellAsk, Trade currTrade){
        if(buyBid == 0 && currTrade.getAction().equals("buy")){ // if the trade is buy, and is market order, should skip checking price
            System.out.println("Doing Market Order");
            return null;
        }

        if(sellAsk == 0 && currTrade.getAction().equals("sell")){ // sell market order
            System.out.println("Doing Market Order");
            return null;
        } 
        
        if(buyBid < sellAsk){    // there is no ask orders below bid, save & return it
            System.out.println("Closing trade");
            tradeRepo.save(currTrade);
            stockController.refreshStockPrice(currTrade.getSymbol(), currTrade.getBid());
            return currTrade;
        }
        return null;
    }

    /**
     * 
     */
    public void makeTrans(Trade currTrade, Account fromAcc, Account toAcc, double transaction_amt) {
        Trans t = new Trans();
        t.setAmount(transaction_amt);
        t.setFrom_account(fromAcc);
        t.setTo_account(toAcc);

        tradeRepo.save(currTrade);
        accController.makeTransaction(t);
    }


    
    public Trade buyMatching(Trade buyTrade, Account cusAcc, Customer customer, int cusId){
        List<Trade> sTrades = sellTradesSorting(buyTrade.getSymbol());  //sorted list of sellTrades
        sTrades.removeIf(t -> (t.getAccount().getCustomer().getId() == cusId));
        sTrades.removeIf(t -> (t.getAsk() == 0));

        System.out.println("Trade Action - " + buyTrade.getAction() + " " + buyTrade.getSymbol());

        double buyBid = buyTrade.getBid();
        int tradeFilledQuantity = buyTrade.getFilled_quantity();
        int initialTradeQty = buyTrade.getQuantity() - buyTrade.getFilled_quantity();
        int currentBuyQty = initialTradeQty;
        //if no trade to be matched, exit
        if(!HaveMatchTrade(sTrades, buyTrade, buyBid)){      
            return buyTrade;
        }

        double lastPrice = 0.0;
        Boolean tradeNotFilled = true;
        int i = 0;
        while(tradeNotFilled && i < sTrades.size()){
            Trade s = sTrades.get(i);
            Customer seller = s.getAccount().getCustomer();
            double sellAsk = s.getAsk();
        
            // check would the trade is able to match, if null, means able to match
            Trade trade = checkHasMatching(buyBid, sellAsk , buyTrade);
            if(trade != null){      //not null means not able to match, return it
                return trade;
            }
            
            String buyStatus = null;       //for update the status of newTrade
            String sellStatus = null;              //for update the status of sTrade
            
            double transaction_amt = 0.0;
            int transaction_qty = 0;
            int sellAvailQty = s.getQuantity() - s.getFilled_quantity();   //available selling quantity
            int sFilledQuantity = s.getFilled_quantity();
            
            int maxBuy = getMaxStock(currentBuyQty, sellAsk, cusAcc.getAvailable_balance());
            //----
            String[] ans = update_Status_Qty(maxBuy, currentBuyQty, sellAvailQty);         
            buyStatus = ans[0];
            sellStatus = ans[1];
            transaction_qty =  Integer.parseInt(ans[2]);

            System.out.println("i:  " + i);
            i++;
            tradeFilledQuantity += transaction_qty ;
            currentBuyQty -= transaction_qty ;
            transaction_amt = transaction_qty * sellAsk;
            lastPrice = sellAsk;

            // Create transaction between buyer and seller
            makeTrans(buyTrade, cusAcc, s.getAccount(), transaction_amt);

            // Only save when transaction is successful ie. theres sufficient funds
            // Update s trade status, fill quantity, avg_price
            sFilledQuantity += transaction_qty ;
            s.setStatus(sellStatus);
            updateAvg_Price(s, lastPrice, transaction_amt, transaction_qty );
            s.setFilled_quantity(sFilledQuantity);

            // Update newTrade trade status, fill quantity, avg_price
            buyTrade.setStatus(buyStatus);
            updateAvg_Price(buyTrade, lastPrice, transaction_amt, transaction_qty );
            buyTrade.setFilled_quantity(tradeFilledQuantity); 

            // Check if new trade is filled
            if(tradeFilledQuantity == initialTradeQty){
                tradeNotFilled = false;
            }
            
            // Save trades
            printTrade(s);
            printTrade(buyTrade);
            tradeRepo.save(s);
            tradeRepo.save(buyTrade);

            //update assests and portfolio
            updateBuyerAssets(customer, buyTrade, transaction_qty , transaction_amt, lastPrice);
            updateSellerAssets(seller, buyTrade, transaction_qty , transaction_amt, lastPrice);
            System.out.println("\nEntering update stock price");
            stockController.refreshStockPrice(buyTrade.getSymbol(), lastPrice);
            System.out.println("Finish update stock price");
        }

        stockController.refreshStockPrice(buyTrade.getSymbol(), lastPrice);
        return buyTrade;
    }


    public Trade sellMatching(Trade sellTrade, Account cusAcc, Customer customer, int cusId){
        List<Trade> bTrades = buyTradesSorting(sellTrade.getSymbol());   //sorted list of buyTrades
        bTrades.removeIf(t -> (t.getAccount().getCustomer().getId() == cusId));
        bTrades.removeIf(t -> (t.getBid() == 0));
        
        System.out.println("\nTrade Action - " + sellTrade.getAction() + " " + sellTrade.getSymbol());

        //for sell action
        System.out.println("In sell");
        double sellAsk = sellTrade.getAsk();
        double lastPrice = 0.0;
        int initialTradeQty = sellTrade.getQuantity() - sellTrade.getFilled_quantity();
        int tradeFilledQuantity = 0;
        int currentSellQty = initialTradeQty;
        
        //if no trade to be matched, exit
        if(!HaveMatchTrade(bTrades, sellTrade, sellAsk)){
            return sellTrade;
        }

        Boolean tradeNotFilled = true;
        int i = 0;
        while(tradeNotFilled && i < bTrades.size()){
            Trade b = bTrades.get(i);
            Customer buyer = b.getAccount().getCustomer();
            double buyBid = b.getBid();

            // check would the trade is able to match, if null, means able to match
            Trade trade =  checkHasMatching(buyBid, sellAsk, sellTrade);
            if(trade != null){
                return trade;
            }

            String sellStatus = null;
            String buyStatus = null;
            double transaction_amt = 0.0;
            int transaction_qty = 0;
            int buyAvailQty = b.getQuantity() - b.getFilled_quantity();   //available selling quantity
            int bFilledQuantity = b.getFilled_quantity();
            
            int maxBuy = getMaxStock(buyAvailQty, buyBid, b.getAccount().getAvailable_balance());
            //----
            String[] ans = update_Status_Qty(maxBuy, buyAvailQty, currentSellQty);         
            buyStatus = ans[0];
            sellStatus = ans[1];
            transaction_qty =  Integer.parseInt(ans[2]);
            
            System.out.println("i:  " + i);
            i++;
            tradeFilledQuantity += transaction_qty ;
            currentSellQty -= transaction_qty ;
            transaction_amt = transaction_qty * buyBid;
            lastPrice = buyBid;

            // Create transaction between buyer and seller
            makeTrans(sellTrade, b.getAccount(), cusAcc, transaction_amt);
            
            // Update b trade status, filled quantity, avg_price
            bFilledQuantity += transaction_qty ;
            b.setStatus(buyStatus);
            updateAvg_Price(b, lastPrice, transaction_amt, transaction_qty );
            b.setFilled_quantity(bFilledQuantity);

            // Update newTrade trade status, filled quantity, avg_price
            sellTrade.setStatus(sellStatus);
            updateAvg_Price(sellTrade, lastPrice, transaction_amt, transaction_qty );
            sellTrade.setFilled_quantity(tradeFilledQuantity);
            
            // Check if new trade is filled
            if(tradeFilledQuantity == initialTradeQty){
                tradeNotFilled = false;
            }

            printTrade(b);
            printTrade(sellTrade);
            // Save trades
            tradeRepo.save(b);
            tradeRepo.save(sellTrade);

            //update assests and portfolio
            updateBuyerAssets(buyer, sellTrade, transaction_qty , transaction_amt, lastPrice);
            updateSellerAssets(customer, sellTrade, transaction_qty , transaction_amt, lastPrice);

            stockController.refreshStockPrice(sellTrade.getSymbol(), lastPrice);
        }
    
        stockController.refreshStockPrice(sellTrade.getSymbol(), lastPrice);
        return sellTrade;
    }

    //match for the trades created during market closing time
    @Override
    public void preMatch(){
        Sort sort = Sort.by("date").ascending();        //Sort by time
        List<Trade> openTrades = tradeRepo.findByStatusContaining("open", sort);

        if(openTrades == null || openTrades.size() == 0){       //if no open trades, directly return
            return;
        }

        for(Trade t: openTrades){       //match all open trades
            Account cusAcc = t.getAccount();
            Customer customer = cusAcc.getCustomer();   
            int customerID = t.getAccount().getCustomer().getId();
    
            if(t.getAction().equals("buy")){
                buyMatching(t, cusAcc, customer, customerID);
            } else{
                sellMatching(t, cusAcc, customer, customerID);
            }
        }
    }
    
    /** 
     * To fullfil the customer's trade order and save the trade info to the repo.
     * Check the validation of the info of stock symbol, time
     * then create trade and perform trade matching
     * @param tradeRecord
     * @return the latest trade info
     */
    @Override
    public Trade TradeGenerate(TradeRecord tradeRecord){
        
        checkSymbol(tradeRecord.getSymbol());       //if stock symbol is invalid, throw InvalidInputException (400)
        checkQuantity(tradeRecord.getQuantity());   //if quantity is not multiple of 100 or is negative number, throw InvalidInputException (400)

        Account cusAcc = accRepo.findById(tradeRecord.getAccount_id()).get();
        Customer customer = cusAcc.getCustomer();   
        
        String action = tradeRecord.getAction();
        Trade trade = new Trade();
        trade.setAction(tradeRecord.getAction());
        trade.setSymbol(tradeRecord.getSymbol());
        if(action.equals("buy")){
            trade.setBid(tradeRecord.getBid());
        }else if(action.equals("sell")){
            trade.setAsk(tradeRecord.getAsk());
        }
        trade.setQuantity(tradeRecord.getQuantity());
        trade.setAvg_price(tradeRecord.getAvg_price());
        trade.setFilled_quantity(tradeRecord.getFilled_quantity());
        trade.setDate(tradeRecord.getDate());
        trade.setAccount(cusAcc);
        trade.setCustomer(customer);
        trade.setStatus("open");

        // check that customer has sufficient balance
        // U only need sufficient balance to buy not to sell
        if(trade.getAction().equals("buy")){
            if(!checkBalance(trade, cusAcc)){
                throw new InsufficientBalanceForTradeException();
            }
        }

        //To check the customer has sufficient stock in assets to sell
        Integer customerID = trade.getCustomer().getId();
        if(trade.getAction().equals("sell")){
            checkAssets(customerID, trade.getSymbol(), trade.getQuantity());
        }
        
        //if stock market is close, save the trade and throw InvalidTradeTiming
        /*
        boolean isValidTime = checkTime();
        if(!isValidTime) {
            return tradeRepo.save(trade);
        }
        */

        // preMatch(cusAcc, customer, customerID);
        preMatch();
        
        // Perform matching trade
        if(trade.getAction().equals("buy")){
            trade = buyMatching(trade, cusAcc, customer, customerID);
        } else{
            trade = sellMatching(trade, cusAcc, customer, customerID);
        }
        return trade;
    }

    private void printTrade(Trade trade){
        System.out.println(trade.getAction());
        System.out.println("avg " + trade.getAvg_price() + " filled " + trade.getFilled_quantity());
    }

}

