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

public class Stock {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    private String symbol;
    private double last_price;
    private int bid_volume;
    private double bid;
    private int ask_volume;
    private double ask;

    public Stock(String symbol, double last_price, int bid_volume, double bid, 
                    int ask_volume, double ask){
        this.symbol = symbol;
        this.last_price = last_price;
        this.bid_volume = bid_volume;
        this.bid = bid;
        this.ask_volume = ask_volume;
        this.ask = ask;
    }
    
}
