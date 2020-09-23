package csd.api.tables;

import java.util.List;
import javax.persistence.*;

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
    
    // private long customer_id;
    private double balance;
    private double available_balance;


    // 
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Trade> trades;

    //
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    //
    @OneToMany(mappedBy = "to_account", cascade = CascadeType.ALL)
    private List<Trans> transactions_to;

    //
    @OneToMany(mappedBy = "from_account", cascade = CascadeType.ALL)
    private List<Trans> transactions_from;

    
    public Account(Customer customer, double balance, double available_balance){
        this.customer = customer;
        this.balance = balance;
        this.available_balance = available_balance;
    }

    /**
     * public Account(long id, double balance, double available_balance){
        this.customer = customer;
        this.balance = balance;
        this.available_balance = available_balance;
    }
     */

    
}