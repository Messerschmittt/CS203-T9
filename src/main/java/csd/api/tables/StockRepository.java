package csd.api.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository <Stock, Integer> {
    Stock findBySymbol(String Symbol);
}
