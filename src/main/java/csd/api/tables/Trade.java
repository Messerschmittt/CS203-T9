package csd.api.tables;

import java.security.Timestamp;
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
public class Trade {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String action;
    private String symbol;
    private int quantity;
    private double bid;
    private double ask;
    private double avg_price;
    private int filled_quantity;
    private String date;
    private Long account_id; // omit
    private Long customer_id; // if we know the account, we know the customer
    private String status;
    //for testing
    private String orderdate;

    // 
    @ManyToOne
    @JoinTable(name = "account") 
    private Account account;


}