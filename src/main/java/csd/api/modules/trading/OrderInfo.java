package csd.api.modules.trading;

import java.util.List;

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

public class OrderInfo{
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String symbol;
    private int quantity;
    private double bid;
    private double ask;
}