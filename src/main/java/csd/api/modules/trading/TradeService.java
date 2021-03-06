package csd.api.modules.trading;

import java.util.List;
import csd.api.tables.Trade;
import csd.api.tables.Customer;
import csd.api.tables.Account;
import csd.api.tables.Assets;
import csd.api.tables.templates.TradeRecord;

public interface TradeService {

    /**
     * Get specific trade record by trade id
     */
    List<Trade> getAllTrades();

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
     * update the status of specific trade record to cancel
     * @param id
     */
    Trade CancelTrade(Integer id);

    /**
     * Check validation of input stock symbol
     * @param symbol
     * @return boolean value
     */
    void checkSymbol(String symbol);

    /**
     * Check does stock market open or not.
     * @return boolean value
     */
    boolean checkTime();

    /**
     * Update the trades status as expired
     */
    void updateStatusToExpired();

    /**
     * Check the validation of input quantity
     * @param quantity
     * @return boolean value
     */
    void checkQuantity(int quantity);

    /**
     * Check the validation of input bid and sell price
     * @param price
     * @return boolean value
     */
    // void checkValidPrice(int price);

    /**
     * check is the customer has enough stock to sell
     * @param customerID
     * @param symbol
     * @param qty
     */
    void checkAssets(int customerID, String symbol, int qty);

    boolean checkBalance(Trade trade, Account cusAcc);

    int getMaxStock(int qty, double price, double availableBalance);
    
    /**
     * Sort the specific stock sell trades with price and time
     * @param symbol
     * @return list of sorted sell Trades
     */
    List<Trade> sellTradesSorting(String symbol);

    /**
     * Sort the specific stock buy trades with price and time
     * @param symbol
     * @return list of sorted buy Trades
     */
    List<Trade> buyTradesSorting(String symbol);

    Assets updateBuyerAssets(Customer customer, Trade newTrade, int transaction_quantity, 
    double transaction_amt, double currentPrice);

    Assets updateSellerAssets(Customer customer, Trade newTrade, int transaction_quantity, 
        double transaction_amt, double currentPrice);


    /**
     * Fullfil the customer trade order
     * @param newTrade
     * @return the updated Trade object
     */
    // Trade matching(Trade newTrade);

    /**
     * Match the trades created in unofficial hours
     */
    // void preMatch(Account cusAcc, Customer customer, int customerID);
    void preMatch();


    /**
     * To fullfil the customer's trade order and save the trade info to the repo
     * @param tradeRecord
     * @return the updated Trade object
     */
    Trade TradeGenerate(TradeRecord tradeRecord);
}