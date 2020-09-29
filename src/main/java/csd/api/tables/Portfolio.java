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

    private double unrealised = 0;
    private double total = 0;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Assests> assests;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("customer_id")
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

    public Portfolio(Customer customer) {
        this.customer = customer;

    }
}
