package csd.api.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

/**
 * We only need this interface declaration
 * Spring will automatically generate an implementation of the repo
 * 
 * JpaRepository provides more features by extending PagingAndSortingRepository, which in turn extends CrudRepository
 * For the purpose of this exercise, CrudRepository would also be sufficient
 */
@Repository
public interface AccountRepository extends JpaRepository <Account, Integer> {
    Account findByCustomer_Id(Integer customer_id);

    List<Account> findAllByCustomer_Id(Integer customer_id);
}
