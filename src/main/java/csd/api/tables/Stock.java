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
    private Long id;
    
    private String symbol;
    private double last_price;
    private int bid_volume;
    private double bidprice;
    private int ask_volume;
    private double askprice;


}
