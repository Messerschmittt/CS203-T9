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
public class Account {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    
    private long customer_id;
    private double balance;
    private double available_balance;

    
    public Account(long customer_id, double balance, double available_balance){
        this.customer_id = customer_id;
        this.balance = balance;
        this.available_balance = available_balance;
    }

    
}