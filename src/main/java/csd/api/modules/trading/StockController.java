package csd.api.modules.trading;

import csd.api.modules.user.UnauthorisedUserException;
import csd.api.tables.*;
import static csd.api.modules.account.RyverBankAccountConstants.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.Authentication;

@RestController
public class StockController {

    public StockRepository stocks;
    public TradeRepository trades;
    public AccountRepository accts;

    public StockController(StockRepository stocks, TradeRepository trades, AccountRepository accts){
        this.stocks = stocks;
        this.trades = trades;
        this.accts = accts;
    }

    /*
    @PostMapping("/stock/initialiseStock")
    public HashSet<Stock> initialiseStock(){
        if(!accts.findById(1).get().getCustomer().getUsername().equals(BANK_USERNAME)){
            throw new RyverBankAccountException();
        }
        
        int quantity = 20000;
        String now = LocalDateTime.now().toString();
        
        // Took out U96.SI 
        ArrayList<String> SGX_top30 = new ArrayList<>(
            Arrays.asList("A17U.SI", "C61U.SI", "C31.SI", "C38U.SI","C09.SI", "C52.SI", "D01.SI", "D05.SI", 
            "G13.SI", "H78.SI", "C07.SI", "J36.SI", "J37.SI", "BN4.SI", "N2IU.SI", "ME8U.SI", "M44U.SI", "O39.SI",
            "S58.SI", "S68.SI", "C6L.SI", "Z74.SI", "S63.SI", "Y92.SI", "U11.SI", "U14.SI", "V03.SI", "F34.SI", "BS6.SI"));
        
        int counter = 0;
        HashSet<Stock> initialisedStock = new HashSet<>();
        for(String symbol : SGX_top30){
            counter++;
            HashMap<String,String> stockInfo = PriceController.getPrice(symbol);        //symbol with .SI

            Create new buy trade
            Trade newBuyTrade = new Trade();
            newBuyTrade.setSymbol(symbol);
            newBuyTrade.setAction("buy");
            newBuyTrade.setDate(now);
            newBuyTrade.setStatus("open");
            newBuyTrade.setQuantity(quantity);
            newBuyTrade.setBid(Double.parseDouble(stockInfo.get("bid")));
            newBuyTrade.setAsk(0.0);
            newBuyTrade.setFilled_quantity(0);
            newBuyTrade.setAccount(accts.findById(1).get()); // Since the RYVERBANK account is the first acct created
            trades.save(newBuyTrade);

            // Create new sell trade
            Trade newSellTrade = new Trade();
            newSellTrade.setSymbol(symbol);
            newSellTrade.setAction("sell");
            newSellTrade.setDate(now);
            newSellTrade.setStatus("open");
            newSellTrade.setQuantity(quantity);
            newSellTrade.setBid(0.0);
            newSellTrade.setAsk(Double.parseDouble(stockInfo.get("ask")));
            newSellTrade.setFilled_quantity(0);
            newSellTrade.setAccount(accts.findById(1).get());
            trades.save(newSellTrade);

            // Create new stock record
            Stock newStock = new Stock(symbol, Double.parseDouble(stockInfo.get("price")),
            quantity, Double.parseDouble(stockInfo.get("bid")), quantity, Double.parseDouble(stockInfo.get("ask")));
            stocks.save(newStock);

            initialisedStock.add(newStock);
        }
        System.out.println("Counter: " + counter);
        return initialisedStock;
    }
    */
    
    // for only role_user
    @GetMapping("/api/stocks")
    public List<Stock> getAllStocks(){
        return stocks.findAll();
    }

    @GetMapping("/api/stocks/{symbol}")
    public Stock getoneStock(@PathVariable String symbol, Authentication auth){
        // Only allow role_user of create stock
        if(!auth.getAuthorities().toString().equals("[ROLE_USER]")){
            throw new UnauthorisedUserException("trade");
        }
        Stock stock = stocks.findBySymbol(symbol);
        if(stock == null) throw new StockNotFoundException(symbol);
        return stock;
    }
    
    
    //Initialise the stock at the start of the program
    public HashSet<Stock> initialiseStock(){
        int quantity = 20000;
        String now = LocalDateTime.now(ZoneOffset.ofHours(8)).toString();
        
        // Took out U96.SI 
        ArrayList<String> SGX_top30 = new ArrayList<>(
            Arrays.asList("A17U.SI", "C61U.SI", "C31.SI", "C38U.SI","C09.SI", "C52.SI", "D01.SI", "D05.SI", 
            "G13.SI", "H78.SI", "C07.SI", "J36.SI", "J37.SI", "BN4.SI", "N2IU.SI", "ME8U.SI", "M44U.SI", "O39.SI",
            "S58.SI", "S68.SI", "C6L.SI", "Z74.SI", "S63.SI", "Y92.SI", "U11.SI", "U14.SI", "V03.SI", "F34.SI", "BS6.SI", "U96.SI"));
        
        int counter = 0;
        HashSet<Stock> initialisedStock = new HashSet<>();
        for(String symbol: SGX_top30){
            counter++;
            HashMap<String,String> stockInfo = PriceController.getPrice(symbol);  //symbol with .SI (To get the stock info from YahooFinance)

            // Create new stock record
            String symbol_ = symbol.substring(0, symbol.length() - 3);          //symbol without .SI (to be store in Stock Repo)
            Stock newStock = new Stock(symbol_, Double.parseDouble(stockInfo.get("price")),
            quantity, Double.parseDouble(stockInfo.get("bid")), quantity, Double.parseDouble(stockInfo.get("ask")));
            stocks.save(newStock);

            // Create new buy trade
            Trade newBuyTrade = new Trade();
            newBuyTrade.setSymbol(symbol_);
            newBuyTrade.setAction("buy");
            newBuyTrade.setDate(now);
            newBuyTrade.setStatus("open");
            newBuyTrade.setQuantity(quantity);
            newBuyTrade.setBid(Double.parseDouble(stockInfo.get("bid")));
            newBuyTrade.setAsk(0.0);
            newBuyTrade.setFilled_quantity(0);
            newBuyTrade.setAccount(BANK_ACCOUNT); // Since the RYVERBANK account is the first acct created
            newBuyTrade.setCustomer(BANK_CUSTOMER);
            trades.save(newBuyTrade);

            // Create new sell trade
            Trade newSellTrade = new Trade();
            newSellTrade.setSymbol(symbol_);
            newSellTrade.setAction("sell");
            newSellTrade.setDate(now);
            newSellTrade.setStatus("open");
            newSellTrade.setQuantity(quantity);
            newSellTrade.setBid(0.0);
            newSellTrade.setAsk(Double.parseDouble(stockInfo.get("ask")));
            newSellTrade.setFilled_quantity(0);
            newSellTrade.setAccount(BANK_ACCOUNT);
            newSellTrade.setCustomer(BANK_CUSTOMER);
            trades.save(newSellTrade);

            initialisedStock.add(newStock);
        }
        System.out.println("Counter: " + counter);
        


        return initialisedStock;
    }

    public void refreshStockPrice(String symbol, double last_price){
        Stock s = stocks.findBySymbol(symbol);
        s.setLast_price(last_price);

        List<Trade> bTrades = trades.findByActionAndStatusAndSymbol("buy","open",symbol);
        List<Trade> bTrades2 = trades.findByActionAndStatusAndSymbol("buy","partial-filled",symbol);
        bTrades.addAll(bTrades2);
        Collections.sort(bTrades);
        Collections.reverse(bTrades);   //descending order
        if(bTrades == null || bTrades.isEmpty()){
            s.setBid_volume(0);
            s.setBid(last_price);
        } else {
            s.setBid(bTrades.get(0).getBid());
            s.setBid_volume(bTrades.get(0).getQuantity()-bTrades.get(0).getFilled_quantity());
        }
        

        List<Trade> sTrades = trades.findByActionAndStatusAndSymbol("sell","open", symbol);
        List<Trade> sTrades2 = trades.findByActionAndStatusAndSymbol("sell","partial-filled", symbol);
        sTrades.addAll(sTrades2);
        Collections.sort(sTrades);
        if(sTrades == null || sTrades.isEmpty()){
            s.setAsk(last_price);
            s.setAsk_volume(0);
        }else{
            s.setAsk(sTrades.get(0).getAsk());
            s.setAsk_volume(sTrades.get(0).getQuantity()-sTrades.get(0).getFilled_quantity());
        }
        

        stocks.save(s);
        // System.out.println("Refreshed Stock: " + s.getSymbol());
        // System.out.println("Bidvol " + s.getBid_volume() + " Bidprice " + s.getBid());
        // System.out.println("Askvol " + s.getAsk_volume() + " Askprice " + s.getAsk());
    }
}
