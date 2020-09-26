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
    private Long id;

    private String code;
    private int quantity;
    private double avg_price;
    private double current_price;
    private double value;
    private double gain_loss;

    // //
    // @ManyToOne
    // @JoinColumn(name = "customer_id")
    // private Customer customer;

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