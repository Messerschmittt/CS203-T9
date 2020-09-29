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
public class Trade implements Comparable<Trade> {

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
    private String status;
  
    @ManyToOne
    @JoinTable(name = "account_id") 
    private Account account;

    public Trade(String action, String symbol, int quanitity, 
    double bid, double ask, double avg_price, int filled_quantity,
    String date, String status, Account account){
        this.action = action;
        this.symbol = symbol;
        this.quantity = quanitity;
        this.bid = bid;
        this.ask = ask;
        this.avg_price = avg_price;
        this.filled_quantity = filled_quantity;
        this.date = date;
        this.status = status;
        this.account = account;
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