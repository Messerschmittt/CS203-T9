package csd.api.tables;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FundTransfer {
    
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private long fromAccNumber;
    private long toAccNumber;
    private double amount;

    public FundTransfer(long fromAccNumber, long toAccNumber){
        this.fromAccNumber = fromAccNumber;
        this.toAccNumber = toAccNumber;
    }
    // transfer 
    public void transfer(long fromAccNumber, long toAccNumber, double amount) throws Exception {
        Account fromAccount = User.retrieveAccount(fromAccNumber);
        Account toAccount = User.retrieveAccount(toAccNumber);

        double balance = fromAccount.getbalance();

        if (balance >= amount) {
            fromAccount.deduct(amount);
            toAccount.add(amount);
        } else {
            throw new Exception("Insufficient funds.");
        }
    }
}
