package csd.api.modules.trading;

import java.util.List;
import csd.api.tables.Trade;
import csd.api.tables.templates.TradeRecord;

public interface TradeService {
    /**
     * Get all trade list in the trade repository
     * @return list of all Trades
     */
    List<Trade> listTrades();

    /**
     * Get specific trade record by trade id
     * @param id
     */
    Trade getTrade(Integer id);

    /**
     * Remove the specific trade record
     * @param id
     */
    void deleteTrade(Integer id);

    /**
     * 
     * @param symbol
     * @return
     */
    boolean checkSymbol(String symbol);

    /**
     * 
     * @return the boolean value that indicate the time is valid or not
     */
    boolean checkTime();
    // bookean checkDate();
    boolean checkQuantity(int quantity);
    List<Trade> sellTradesSorting(String symbol);
    List<Trade> buyTradesSorting(String symbol);
    Trade matching(Trade newTrade);
    Trade TradeGenerate(TradeRecord tradeRecord);
}