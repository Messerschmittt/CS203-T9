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
public class Trans {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
     
    private long from_acc; // omit
    private long to_acc; // omit
    private double amount;

    //
    @ManyToOne
    @JoinColumn(name = "account_to_id")
    private Account to_account;
    
    //
    @ManyToOne
    @JoinColumn(name = "account_from_id")
    private Account from_account;
    

    public Trans(long from, long to, double amount){
        this.from_acc = from;
        this.to_acc = to;
        this.amount = amount;
    }

    
}