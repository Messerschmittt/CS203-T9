package csd.api.modules.trading;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import csd.api.tables.TradeRepository;
import csd.api.tables.Account;
import csd.api.tables.AccountRepository;
import csd.api.tables.Trade;
import csd.api.tables.TradeController;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@RestController
public class PriceController{
    private TradeRepository tradeRepo;
    private AccountRepository accRepo;
    private TradeController trades;

    public PriceController(TradeRepository trades, AccountRepository accRepo){
        this.tradeRepo = trades;
        this.accRepo = accRepo;
    }

    @GetMapping("/pricetest")
    public static HashMap<String, String> getPrice(@RequestParam(name = "stock") String symbol){
        try{
            System.out.println(symbol);
            Stock stock = YahooFinance.get(symbol);
            // stock.setStockExchange("SI");
            BigDecimal ask = stock.getQuote().getAsk();
            Long askVol = stock.getQuote().getAskSize();
            BigDecimal bid = stock.getQuote().getBid();
            Long bidVol = stock.getQuote().getBidSize();
            BigDecimal price = stock.getQuote().getPrice();
            LocalDateTime now = LocalDateTime.now();

            HashMap<String, String> info = new HashMap<>();
            info.put("ask" , ask.toString());
            info.put("askVol" , askVol.toString());
            info.put("bid" , bid.toString());
            info.put("bidVol" , bidVol.toString());
            info.put("price" , price.toString());
            info.put("date", now.toString());
            info.put("symbol", symbol);

            return info;
            
        }catch(IOException e){
            System.out.println("Getting prices from API failed");
        }
        return null;

    }

    //Weijie
    @PostMapping("/generatefake")  //
    public void testGenerate(){
        generateTrade("buy", "INTC");
        generateTrade("buy", "INTC");
        generateTrade("sell", "MSFT");
    }
    
    @PostMapping("/buy/{acc_id}")
    public void BuyGenerate(@PathVariable Integer acc_id,@Valid @RequestBody Trade trade){
        //to check is the quantity is multiple of 100
        if(!checkQuantity(trade.getQuantity())){
            System.out.println("Input quantity is not valid");
            return;
        }

        //to check have enough balance
        double total_bid_price = trade.getQuantity()*trade.getBid();
        if(checkBalance(acc_id, total_bid_price)){      //have enough balance
            //need to get the selling stock
            
            generateTrade("buy", trade.getSymbol());
        }
        
    }


    @PostMapping("/sell/{acc_id}")
    public void SellGenerate(@PathVariable Long acc_id,@Valid @RequestBody Trade trade){
        //to check is the quantity is multiple of 100
        if(!checkQuantity(trade.getQuantity())){
            System.out.println("Input quantity is not valid");
            return;
        }

        generateTrade("sell", trade.getSymbol());
    }
    
    public void matching(@PathVariable Long acc_id,@Valid @RequestBody Trade trade){
        String date = trade.getDate().substring(0, 10);
        List<Trade> orders = trades.getAllmatchingorder(trade.getAction(),date,trade.getSymbol());

        double max = 0;
        for(Trade t: orders){
            if(t.getBid() >= trade.getAsk() && t.getBid() > max){
                max = t.getBid();
            }
        }
        List<Trade> mList = tradeRepo.findBySymbolAndBid(trade.getSymbol(), max);
        LocalDateTime edate = LocalDateTime.now();

        for(Trade oinfo: mList){
            if(LocalDateTime.parse(oinfo.getDate()).compareTo(edate) < 0){
                edate = LocalDateTime.parse(oinfo.getDate());
            }
        }
    }
    //check the customer have enough balance for trading (buying)
    public boolean checkBalance(Integer acc_id, double total_bid_price){
        Optional<Account> acc = accRepo.findById(acc_id);
        Account account = acc.get();
        double availbalance = account.getAvailable_balance();
        if(availbalance >= total_bid_price){
            return true;
        }
        return false;
    }

    //check the input quantity is multiple of 100
    public boolean checkQuantity(int quantity){
        if(quantity % 100 == 0){
            return true;
        }
        return false;
    }

    //means trade match -> proceed order
    public Trade generateTrade(String action, String symbol){
        HashMap<String, String> info = getPrice(symbol);
        Trade newTrade = new Trade();
        newTrade.setAction(action);
        newTrade.setSymbol(info.get("symbol"));
        if(action.equals("buy")){
            newTrade.setBid(Double.parseDouble(info.get("bid")));
            newTrade.setQuantity(Integer.parseInt(info.get("bidVol")));
        }else if(action.equals("sell")){
            newTrade.setBid(Double.parseDouble(info.get("ask")));
            newTrade.setQuantity(Integer.parseInt(info.get("askVol")));
        }
        newTrade.setAccount_id(Integer.parseInt("-1"));
        newTrade.setCustomer_id(Integer.parseInt("-1"));
        newTrade.setDate(info.get("date"));
        //newTrade.setDate(info.get("date").substring(0, 10));

        return tradeRepo.save(newTrade);
        
    }


}