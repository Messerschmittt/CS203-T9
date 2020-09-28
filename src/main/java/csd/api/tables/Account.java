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
public class Account {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;    
    
    private double balance = 0.0;
    private double available_balance = 0.0;


    // 
    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trade> trades;

    //
    @ManyToOne
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