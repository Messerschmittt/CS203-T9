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
public class Account {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;    
    
    private double balance = 0.0;
    private double available_balance = 0.0;


    // 
    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trade> trades;

    //This complex mess is so that it only shows the id instead of the entire object
    // unsure how much of it is really relevant but whatever
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("customer_id")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    //
    @OneToMany(mappedBy = "to_account", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trans> transactions_to;

    //
    @OneToMany(mappedBy = "from_account", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trans> transactions_from;

    
    public Account(Customer customer, double balance, double available_balance){
        this.customer = customer;
        this.balance = balance;
        this.available_balance = available_balance;
    }



    
}