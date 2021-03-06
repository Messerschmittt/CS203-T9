package csd.api.tables;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

/**
 * We only need this interface declaration
 * Spring will automatically generate an implementation of the repo
 * 
 * JpaRepository provides more features by extending PagingAndSortingRepository, which in turn extends CrudRepository
 * For the purpose of this exercise, CrudRepository would also be sufficient
 */
@Repository
public interface TradeRepository extends JpaRepository <Trade, Integer> {
    List<Trade> findByActionAndDateAndSymbol(String action, String date, String symbol);
    List<Trade> findBySymbolAndBid(String Symbol, double bid);
    List<Trade> findByActionAndStatusAndSymbol(String action, String status, String symbol);
    List<Trade> findByActionAndSymbolAndStatus(String action, String symbol, String status1);
    List<Trade> findByStatusContainingOrStatusContaining(String status1,String status2);
    List<Trade> findByStatusContaining(String status, Sort sort);
    List<Trade> findByCustomer_IdAndSymbolAndStatus(Integer customerId, String symbol, String status);
    List<Trade> findBySymbol(String symbol);
}
