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

public class Portfolio {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    private double unrealised;
    private double total;
    
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // //calculate unrealised gain and loss from current stock
    // public double CalculateUnrealised(){
    //     unrealised = 0;
    //     for(Assests a: assests){
    //         unrealised += a.getGain_loss();
    //     }
    //     return unrealised;
    // }
}
