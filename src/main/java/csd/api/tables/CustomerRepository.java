package csd.api.tables;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * We only need this interface declaration
 * Spring will automatically generate an implementation of the repo
 * 
 * JpaRepository provides more features by extending PagingAndSortingRepository, which in turn extends CrudRepository
 * For the purpose of this exercise, CrudRepository would also be sufficient
 */
@Repository
public interface CustomerRepository extends JpaRepository <Customer, Integer> {
    // @Query(value = "Select u from Customer where u.application_User_id =?1", nativeQuery = true)
    // Customer findByApplication_User_Id(Integer application_User_id);
    Customer findByUsername(String username);
    // Customer findById(Integer id);
    boolean existsByUsername(String username);
    boolean existsByNric(String nric);



}
