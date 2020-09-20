package csd.api.modules.trading;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import csd.api.tables.TradeRepository;
import csd.api.tables.Trade;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@RestController
public class PriceController{
    private TradeRepository tradeRepo;
    public PriceController(TradeRepository trades){
        this.tradeRepo = trades;
    }

    @GetMapping("/pricetest")
    public static HashMap<String, String> getPrice(@RequestParam(name = "stock") String symbol){
        try{
            Stock stock = YahooFinance.get(symbol);
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
            
        }catch(Exception e){
            System.out.println("Getting prices from API failed");
        }
        return null;

    }

    @PostMapping("/generatefake")
    public void testGenerate(){
        generateOrder("bid", "INTC");
        generateOrder("ask", "MSFT");
    }

    public Trade generateOrder(String action, String symbol){
        HashMap<String, String> info = getPrice(symbol);
        Trade newTrade = new Trade();
        newTrade.setAction(action);
        newTrade.setSymbol(info.get("symbol"));
        if(action.equals("bid")){
            newTrade.setBid(Double.parseDouble(info.get("bid")));
            newTrade.setQuantity(Integer.parseInt(info.get("bidVol")));
        }else if(action.equals("ask")){
            newTrade.setBid(Double.parseDouble(info.get("ask")));
            newTrade.setQuantity(Integer.parseInt(info.get("askVol")));
        }
        newTrade.setAccount_id(Long.parseLong("-1"));
        newTrade.setCustomer_id(Long.parseLong("-1"));
        newTrade.setDate(info.get("date"));

        return tradeRepo.save(newTrade);
        
    }


}