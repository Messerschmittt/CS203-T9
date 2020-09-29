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
public class Trans {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    private double amount;

    //
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("account_to_id")
    @JoinColumn(name = "account_to_id")
    private Account to_account;
    
    //
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("account_from_id")
    @JoinColumn(name = "account_from_id")
    private Account from_account;
    
}