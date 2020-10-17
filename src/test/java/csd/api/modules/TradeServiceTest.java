package csd.api.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.api.tables.*;
import csd.api.tables.templates.TradeRecord;
import csd.api.modules.*;
import csd.api.modules.account.AccountController;
import csd.api.modules.trading.StockController;
import csd.api.modules.trading.TradeServiceImp;
import csd.api.modules.user.InvalidInputException;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {
    @Mock
    private TradeRepository trades;
    
    @Mock
    private AccountRepository accounts;

    @Mock
    private PortfolioRepository portfolios;

    @Mock
    private AssetsRepository assets;

    @Mock
    private CustomerRepository customers;

    @Mock
    private StockRepository stocks;

    @Mock
    private AccountController aController;

    @Mock
    private StockController sController;

    @InjectMocks
    private TradeServiceImp tradeservice;

    @Test
    void addTrade_validSymbol_ReturnTrade() {
        // arrange ***
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setAction("buy");
        tradeRecord.setSymbol("A17U.SI");
        tradeRecord.setQuantity(500);
        tradeRecord.setBid(3.2);
        tradeRecord.setAccount_id(1);
        tradeRecord.setCustomer_id(1);
        
        Stock stock = new Stock();      //
        when(stocks.findBySymbol(any(String.class))).thenReturn(stock);
        
        Trade savedtrade = tradeservice.TradeGenerate(tradeRecord);
        assertNotNull(savedtrade);
        verify(stocks).findBySymbol(tradeRecord.getSymbol());
    }

    @Test
    void addTrade_InvalidSymbol_ReturnNull() {
        // arrange ***
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setAction("buy");
        tradeRecord.setSymbol("ss");
        tradeRecord.setQuantity(500);
        tradeRecord.setBid(3.2);
        tradeRecord.setAccount_id(1);
        tradeRecord.setCustomer_id(1);

        when(stocks.findBySymbol(any(String.class))).thenReturn(null);

        Trade savedtrade = null;
        try {
            savedtrade =  tradeservice.TradeGenerate(tradeRecord);
        } catch (InvalidInputException e) {
            assertNull(savedtrade);
            verify(stocks).findBySymbol(tradeRecord.getSymbol());
        }
    }

    @Test
    void addTrade_InvalidQuantity_ReturnTrade() {
        // arrange ***
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setAction("sell");
        tradeRecord.setSymbol("A17U.SI");
        tradeRecord.setQuantity(50);
        tradeRecord.setAsk(2);
        tradeRecord.setAccount_id(1);
        tradeRecord.setCustomer_id(1);
        
        // Trade trade = new Trade(tradeRecord.getAction(), tradeRecord.getSymbol(), tradeRecord.getQuantity(), tradeRecord.getBid(), tradeRecord.getAsk(), 
        // tradeRecord.getAvg_price(), tradeRecord.getFilled_quantity(), tradeRecord.getDate(), tradeRecord.getStatus(),  cusAcc);

        Trade trade = new Trade();
        
        when(tradeservice.checkSymbol(any(String.class))).thenReturn(true);
        when(tradeservice.checkTime()).thenReturn(true);
        // when(tradeservice.checkQuantity(any(Integer.class))).thenReturn(false);
        
        Trade savedtrade = null;
        try {
            savedtrade =  tradeservice.TradeGenerate(tradeRecord);
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            assertNull(savedtrade);
        }
    }
}
