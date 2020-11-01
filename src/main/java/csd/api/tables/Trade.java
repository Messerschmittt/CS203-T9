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

import com.fasterxml.jackson.annotation.*;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Trade implements Comparable<Trade> {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String action;
    private String symbol;
    private int quantity;
    private double bid = 0.0;
    private double ask = 0.0;
    private double avg_price = 0.0;
    private int filled_quantity = 0;
    private String date;
    
    
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("account_id")
    @JoinColumn(name = "account_id") 
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("customer_id")
    @JoinColumn(name = "customer_id") 
    private Customer customer;

    private String status = "open";
    // @ManyToOne
    // @JoinColumn(name = "customer_id") 
    // private Customer customer;


    // public Trade(String action, String symbol, int quanitity, 
    // double bid, double ask, double avg_price, int filled_quantity,
    // String date, int accountid, int customerid, String status){
    public Trade(String action, String symbol, int quanitity, 
    double bid, double ask, double avg_price, int filled_quantity,
    String date, Account account,Customer customer,String status){
        this.action = action;
        this.symbol = symbol;
        this.quantity = quanitity;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filled_quantity = filled_quantity;
        this.date = date;
        this.account = account;
        this.customer = customer;
        this.status = status;
    }

    @Override
    public int compareTo(Trade o) {
        if(o.getAction().equals("buy")){
            return (int)Double.compare(this.getBid(), o.getBid());
        }else{
            return (int)Double.compare(this.getAsk(), o.getAsk());
        }
        
    }
}