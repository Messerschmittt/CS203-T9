package csd.api.tables;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * We only need this interface declaration
 * Spring will automatically generate an implementation of the repo
 * 
 * JpaRepository provides more features by extending PagingAndSortingRepository, which in turn extends CrudRepository
 * For the purpose of this exercise, CrudRepository would also be sufficient
 */
@Repository
public interface TradeRepository extends JpaRepository <Trade, Long> {
    List<Trade> findByActionAndOrderdateAndSymbol(String action, String orderdate, String symbol);
    List<Trade> findBySymbolAndBid(String Symbol, double bid);
	List<Trade> findByActionAndStatusAndSymbol(String action, String status, String symbol);
}
