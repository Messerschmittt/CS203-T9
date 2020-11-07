package csd.api.tables;

import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.*;


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


    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Assets> assets;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("customer_id")
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    private double unrealized_gain_loss = 0.0;
    private double realized_gain_loss = 0.0;
    private double total_gain_loss = 0.0;


    public Portfolio(Customer customer) {
        this.customer = customer;

    }

    //calculate unrealised gain and loss from current stock holding
    public double updateUnrealised(){
        if(assets == null || assets.isEmpty()){
            return unrealized_gain_loss;
        }

        unrealized_gain_loss = 0.0;   //reset to 0 and recalculate
        for(Assets a: assets){
            unrealized_gain_loss += a.getGain_loss();
        }
        return unrealized_gain_loss;
    }

    public double updateTotal_gain_loss(double realised_gain_loss){
        updateUnrealised();
        this.realized_gain_loss += realised_gain_loss;
        this.total_gain_loss = this.realized_gain_loss + this.unrealized_gain_loss;
        return this.total_gain_loss;
    }
}
