package csd.api.tables;

import java.util.List;
import javax.persistence.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne; //
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Assests {
    
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private int quantity;
    private double avg_price;
    private double current_price;
    private double value;
    private double gain_loss;

    //
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    @JsonIgnore
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    public Assests(String code, int quantity, double avg_price, double current_price){
        this.code = code;
        this.quantity = quantity;
        this.avg_price = avg_price;
        this.current_price = current_price;
        CalculateValue();
        CalculateGain_loss();
    }

    //calculate gain_loss
    public double CalculateGain_loss(){
        gain_loss = (current_price - avg_price) * quantity;
        return gain_loss;
    }

    //calculate value
    public double CalculateValue(){
        value = current_price * quantity;
        return value;
    }
    
}