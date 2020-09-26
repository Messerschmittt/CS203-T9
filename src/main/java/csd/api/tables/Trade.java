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
    private Integer id;
    
    private String action;
    private String symbol;
    private int quantity;
    private double bid = -1;
    private double ask = -1;
    private double avg_price;
    private int filled_quantity = 0;
    private String date;
    private Integer account_id; // omit
    private Integer customer_id; // if we know the account, we know the customer
    private String status;

    // 
    @ManyToOne
    @JoinTable(name = "account_id") 
    private Account account;

}