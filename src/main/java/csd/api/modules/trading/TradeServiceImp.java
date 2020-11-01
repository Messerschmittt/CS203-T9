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

     
    //for ROLE_USER
    @Override
    public List<Trade> getAllTrades(){
        return tradeRepo.findAll();
    }

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

    /**
     * cancel the specific trade record using trade id
     * If there is no trade with the given "id", throw a TradeNotFoundException
     * @param id
     */
    @Override
    public Trade CancelTrade(Integer id){
        Trade trade = tradeRepo.findById(id).get();
        String tradestatus =  trade.getStatus();
        if(tradestatus.equals("open")){
                tradeRepo.findById(id).map(t -> {t.setStatus("cancelled");
                return tradeRepo.save(t);
            });

            Account cusAcc = trade.getAccount();
            cusAcc.setAvailable_balance(cusAcc.getAvailable_balance()+(trade.getBid()*trade.getQuantity()));
        }

        
        return trade;
    }

    /**
     * Check the validation of input stock symbol
     * @param symbol
     * @return boolean value
     */
    @Override
    public boolean checkSymbol(String symbol){
        //check symbol 
        boolean isValid = true;
        if(stockRepo.findBySymbol(symbol) == null){
            isValid = false;
        }
        return isValid;
    }

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
    
        double marketPrice = stockRepo.findBySymbol(trade.getSymbol()).getBid();
        
        List<Trade> allSellTradesForStock = tradeRepo.findBySymbol(trade.getSymbol());
        allSellTradesForStock.removeIf(t -> t.getAsk() == 0.0);
        double marketCap = 0;
        for(Trade t : allSellTradesForStock){
            marketCap += (t.getQuantity() - t.getFilled_quantity())*t.getAsk();
        }

        double available = cusAcc.getAvailable_balance();
        // if(trade.getBid() == 0 && marketCap > available){
        //     return false;
        // }
        if(trade.getBid() * trade.getQuantity() > available){
            return false;
        }
        // reduce avaialble balance of customer
        
        cusAcc.setAvailable_balance(cusAcc.getAvailable_balance() - trade.getBid() * trade.getQuantity());

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
                cusAcc.setAvailable_balance(cusAcc.getAvailable_balance()+(t.getQuantity()-t.getFilled_quantity())*t.getBid());
            }
        }
    }

    /**
     * Check the validation of input quantity. Quantity should be multiple of 100 (and not negative)
     * @param quantity
     * @return boolean value
     */
    @Override
    public boolean checkQuantity(int quantity){
        if(quantity % 100 == 0 && quantity > 0){
            return true;
        }
        return false;
    }

    /**
     * Check the validation of input bid and sell price. Should not be negative
     * @param price
     * @return boolean value
     */
    @Override
    public boolean checkValidPrice(int price){
        if(price >= 0){
            return true;
        }
        return false;
    }

    
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

    //update both side of sell and buy average price
    public void updateAvg_Price(Trade trade, double price, double transaction_amt, int transaction_quantity){
        double avg_price = trade.getAvg_price();
        if(avg_price == 0){
            trade.setAvg_price(price);
        } else{
            trade.setAvg_price((avg_price * trade.getFilled_quantity() + transaction_amt) / (transaction_quantity + trade.getFilled_quantity()));
        }
    }

    @Override
    public Assets updateBuyerAssets(Customer customer, Trade newTrade, int transaction_quantity, 
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
            a.setQuantity(transaction_quantity);
        } else { // update assets
            int priorQuantity = a.getQuantity();
            double priorAvgPrice = a.getAvg_price();
            double newAvgPrice = ((priorQuantity * priorAvgPrice) + transaction_amt)
                    / (transaction_quantity + priorQuantity);
            a.setAvg_price(newAvgPrice);
            a.setQuantity(priorQuantity + transaction_quantity);
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
    public Assets updateSellerAssets(Customer customer, Trade newTrade, int transaction_quantity, 
    double transaction_amt, double currentPrice){
        if(customer.getId() == BANK_CUSTOMER.getId()){
            return null;
        }
        // Create/Update asset record
        Assets a = assetsRepo.findByCustomer_IdAndCode(customer.getId(), newTrade.getSymbol());
        int priorQuantity = a.getQuantity();
        double priorAvgPrice = a.getAvg_price();
        int newQuantity = priorQuantity - transaction_quantity;
        // System.out.println("after sell quantity" + newQuantity);
        
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


   //find the matching trade   
    @Override  
    public Trade matching(Trade newTrade){
        Account cusAcc = newTrade.getAccount();
        Customer customer = cusAcc.getCustomer();   
        
        int cusId = newTrade.getAccount().getCustomer().getId();
        List<Trade> sTrades = sellTradesSorting(newTrade.getSymbol());  //sorted list of sellTrades
        sTrades.removeIf(t -> (t.getAccount().getCustomer().getId() == cusId));
        List<Trade> bTrades = buyTradesSorting(newTrade.getSymbol());   //sorted list of buyTrades
        bTrades.removeIf(t -> (t.getAccount().getCustomer().getId() == cusId));
        
        Boolean tradeNotFilled = true;
        int i = 0;
        int initialTradeQty = newTrade.getQuantity() - newTrade.getFilled_quantity();
        double lastPrice = 0.0;
        System.out.println("Trade Action - " + newTrade.getAction() + " " + newTrade.getSymbol());
        if(newTrade.getAction().equals("buy")){
            lastPrice = newTrade.getBid();
            if(sTrades == null || sTrades.size() == 0){     //no matching trade
                tradeRepo.save(newTrade);
                stockController.refreshStockPrice(newTrade.getSymbol(), newTrade.getBid());
                return newTrade;
            }
            System.out.println("selltrades- " + sTrades.get(0).getAction() + sTrades.get(0).getSymbol());

            double newTradeBid = newTrade.getBid();
            int tradeFilledQuantity = newTrade.getFilled_quantity();
            int currentTradeQty = initialTradeQty;
            while(tradeNotFilled && i < sTrades.size()){
                Trade s = sTrades.get(i);
                Customer seller = s.getAccount().getCustomer();
                double sAskPrice = s.getAsk();

                if(newTrade.getBid() == 0){ // if market order can skip the price checking
                    System.out.println("Doing Market Order");
                }else if(newTradeBid < sAskPrice){ // once there are no more ask orders below bid --> save & return
                    System.out.println("Closing trade");
                    tradeRepo.save(newTrade);
                    stockController.refreshStockPrice(newTrade.getSymbol(), newTrade.getBid());
                    return newTrade;
                }

                String newTradeStatus = null;       //for update the status of newTrade
                String sStatus = null;              //for update the status of sTrade
                double transaction_amt = 0.0;
                int transaction_quantity = 0;
                int sAvailQuantity = s.getQuantity() - s.getFilled_quantity();   //available selling quantity
                int sFilledQuantity = s.getFilled_quantity();
                
                int maxBuy = getMaxStock(currentTradeQty, sAskPrice, cusAcc.getAvailable_balance());
                if(maxBuy == currentTradeQty){
                    if( (sAvailQuantity < currentTradeQty) ){  //partially filled in newTrade(buy), but sell trade -> "filled"
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
                } else{     //if (maxBuy < currentTradeQty)
                    if( sAvailQuantity <= maxBuy ){  //partially filled in newTrade(buy), but sell trade -> "filled"
                        sStatus = "filled";
                        newTradeStatus = "partial-filled";
                        transaction_quantity = sAvailQuantity;
                    } else if(sAvailQuantity > maxBuy){  //partially filled in both side
                        sStatus = "partial-filled";
                        newTradeStatus = "partial-filled";
                        transaction_quantity = maxBuy;
                    }
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
                // can delete ?????
                // if(s.getAccount() == null){
                //     System.out.println("Faulty account accessed");
                //     t.setTo_account(new Account());
                // }
                tradeRepo.save(newTrade);
                accController.makeTransaction(t);
                
                // Only save when transaction is successful ie. theres sufficient funds
                // Update s trade status, fill quantity, avg_price
                sFilledQuantity += transaction_quantity;
                s.setStatus(sStatus);
                updateAvg_Price(s, lastPrice, transaction_amt, transaction_quantity);
                s.setFilled_quantity(sFilledQuantity);

                // Update newTrade trade status, fill quantity, avg_price
                newTrade.setStatus(newTradeStatus);
                updateAvg_Price(newTrade, lastPrice, transaction_amt, transaction_quantity);
                newTrade.setFilled_quantity(tradeFilledQuantity); 

                // Check if new trade is filled
                if(tradeFilledQuantity == initialTradeQty){
                    tradeNotFilled = false;
                }
                
                // Save trades
                printTrade(s);
                printTrade(newTrade);
                tradeRepo.save(s);
                tradeRepo.save(newTrade);

                //update assests and portfolio
                updateBuyerAssets(customer, newTrade, transaction_quantity, transaction_amt, lastPrice);
                updateSellerAssets(seller, newTrade, transaction_quantity, transaction_amt, lastPrice);
                System.out.println("Entering update stock price");
                stockController.refreshStockPrice(newTrade.getSymbol(), lastPrice);
                System.out.println("Finish update stock price");
            }
        }
        
        //for sell action
        if(newTrade.getAction().equals("sell")){
            System.out.println("In sell");
            lastPrice = newTrade.getAsk();
            if(bTrades == null || bTrades.size() == 0){     //no matching trade
                tradeRepo.save(newTrade);
                System.out.println("refersing" + newTrade.getSymbol() + "- " + newTrade.getAsk());
                stockController.refreshStockPrice(newTrade.getSymbol(), newTrade.getAsk());
                return newTrade;
            }
            System.out.println(bTrades.get(0).getSymbol() + '-' + bTrades.get(0).getBid()+ "-" + bTrades.get(0).getQuantity());
            double newTradeAsk = newTrade.getAsk();
            int tradeFilledQuantity = 0;
            int currentTradeQty = initialTradeQty;
            while(tradeNotFilled && i < bTrades.size()){
                Trade b = bTrades.get(i);
                Customer buyer = b.getAccount().getCustomer();
                double bBidPrice = b.getBid();

                if(newTradeAsk == 0){ // skip price checking for market order

                }
                else if(newTradeAsk > bBidPrice){ // once there are no more bid orders above the ask --> save & return
                    tradeRepo.save(newTrade);
                    stockController.refreshStockPrice(newTrade.getSymbol(), newTrade.getAsk());
                    return newTrade;
                }

                String newTradeStatus = null;
                String bStatus = null;
                double transaction_amt = 0.0;
                int transaction_quantity = 0;
                int bAvailQuantity = b.getQuantity() - b.getFilled_quantity();   //available selling quantity
                int bFilledQuantity = b.getFilled_quantity();
                
                int maxBuy = getMaxStock(bAvailQuantity, bBidPrice, b.getAccount().getAvailable_balance());
                if(maxBuy == bAvailQuantity){
                    if( (bAvailQuantity > currentTradeQty) ){
                        bStatus = "partial-filled";
                        newTradeStatus = "filled";
                        transaction_quantity = currentTradeQty;
                    } else if(bAvailQuantity < currentTradeQty){
                        bStatus = "filled";
                        newTradeStatus = "partial-filled";
                        transaction_quantity = bAvailQuantity;

                    } else if(bAvailQuantity == currentTradeQty){
                        bStatus = "filled";
                        newTradeStatus = "filled";
                        transaction_quantity = currentTradeQty;

                    }
                } else{     // (maxBuy < bAvailQuantity)
                    if(maxBuy >= currentTradeQty){
                        bStatus = "partial-filled";
                        newTradeStatus = "filled";
                        transaction_quantity = currentTradeQty;
                    } else if(maxBuy < currentTradeQty){
                        bStatus = "partial-filled";
                        newTradeStatus = "partial-filled";
                        transaction_quantity = maxBuy;
                    }
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

                // can delete ?????
                // if(b.getAccount() == null){
                //     System.out.println("Bank account accessed");
                //     t.setFrom_account(new Account(null, 1_000_000, 1_000_000)); //using an arbitary value for bank
                // }
                tradeRepo.save(newTrade);
                accController.makeTransaction(t);
                
                // Update b trade status, filled quantity, avg_price
                bFilledQuantity += transaction_quantity;
                b.setStatus(bStatus);
                updateAvg_Price(b, lastPrice, transaction_amt, transaction_quantity);
                b.setFilled_quantity(bFilledQuantity);

                // Update newTrade trade status, filled quantity, avg_price
                newTrade.setStatus(newTradeStatus);
                updateAvg_Price(newTrade, lastPrice, transaction_amt, transaction_quantity);
                newTrade.setFilled_quantity(tradeFilledQuantity);
                
                // Check if new trade is filled
                if(tradeFilledQuantity == initialTradeQty){
                    tradeNotFilled = false;
                }

                printTrade(b);
                printTrade(newTrade);
                // Save trades
                tradeRepo.save(b);
                tradeRepo.save(newTrade);

                //update assests and portfolio
                updateBuyerAssets(buyer, newTrade, transaction_quantity, transaction_amt, lastPrice);
                updateSellerAssets(customer, newTrade, transaction_quantity, transaction_amt, lastPrice);

                stockController.refreshStockPrice(newTrade.getSymbol(), lastPrice);
            }
        }
        
        stockController.refreshStockPrice(newTrade.getSymbol(), lastPrice);
        return newTrade;
    } 

    //match for the trades created during market closing time
    @Override
    public void preMatch(){
        Sort sort = Sort.by("date").ascending();        //Sort by time
        List<Trade> openTrades = tradeRepo.findByStatusContaining("open", sort);

        if(openTrades != null && openTrades.size() != 0){
            for(Trade t: openTrades){
                matching(t);
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
        
        boolean isValidSymbol = checkSymbol(tradeRecord.getSymbol());
        boolean isValidQty = checkQuantity(tradeRecord.getQuantity());

        //if stock symbol is invalid, throw InvalidInputException
        if(!isValidSymbol){
            throw new InvalidInputException(tradeRecord.getSymbol(), "Stock Symbol ");
        }

        //if the quantity is not multiple of 100, throw InvalidInputException
        String qty = "" + tradeRecord.getQuantity();
        if(!isValidQty){
            throw new InvalidInputException(qty, "Input quantity. It should be multiiple of 100 and positive number");
        }

        Account cusAcc = accRepo.findById(tradeRecord.getAccount_id()).get();
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
        trade.setCustomer(cusAcc.getCustomer());
        trade.setStatus(tradeRecord.getStatus());

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

        // Add a function to match all open trades
        // if(checktiming()) -> match
        
        //if stock market is close, save the trade and throw InvalidTradeTiming
        boolean isValidTime = checkTime();
        if(!isValidTime) {
            return tradeRepo.save(trade);
        }
        preMatch();
        
        // Enter Create and Matching Function (return the latest trade info)
        return matching(trade);
    }

    private void printTrade(Trade trade){
        System.out.println(trade.getAction());
        System.out.println("avg " + trade.getAvg_price() + " filled " + trade.getFilled_quantity());
    }

}

