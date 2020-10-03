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
    
    private double unrealised = 0.0;
    private double total = 0.0;

    public Portfolio(Customer customer) {
        this.customer = customer;

    }

    //calculate unrealised gain and loss from current stock holding
    public double updateUnrealised(){
        if(assets == null || assets.isEmpty()){
            return unrealised;
        }

        unrealised = 0.0;   //reset to 0 and recalculate
        for(Assets a: assets){
            unrealised += a.getGain_loss();
        }
        return unrealised;
    }
}
