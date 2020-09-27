package csd.api.modules.trading;

import csd.api.tables.*;

import java.time.LocalDateTime;
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

@RestController
public class StockController {

    public StockRepository stocks;
    public TradeRepository trades;

    public StockController(StockRepository stocks, TradeRepository trades){
        this.stocks = stocks;
        this.trades = trades;
    }

    @PostMapping("/stock/initialiseStock")
    public HashSet<Stock> initialiseStock(){
        int quantity = 20_000;
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
            HashMap<String,String> stockInfo = PriceController.getPrice(symbol);

            // Create new buy trade
            Trade newBuyTrade = new Trade();
            newBuyTrade.setSymbol(symbol);
            newBuyTrade.setAction("buy");
            newBuyTrade.setDate(now);
            newBuyTrade.setStatus("open");
            newBuyTrade.setQuantity(quantity);
            newBuyTrade.setBid(Double.parseDouble(stockInfo.get("bid")));
            newBuyTrade.setAsk(0.0);
            newBuyTrade.setFilled_quantity(0);
            newBuyTrade.setAccount(null);
            trades.save(newBuyTrade);

            // Create new sell trade
            Trade newSellTrade = new Trade();
            newSellTrade.setSymbol(symbol);
            newSellTrade.setAction("sell");
            newSellTrade.setDate(now);
            newSellTrade.setStatus("open");
            newSellTrade.setQuantity(quantity);
            newSellTrade.setBid(Double.parseDouble(stockInfo.get("bid")));
            newSellTrade.setAsk(0.0);
            newSellTrade.setFilled_quantity(0);
            newSellTrade.setAccount(null);
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

    @GetMapping("/stocks")
    public List<Stock> getAllStocks(){
        return stocks.findAll();
    }
}
