// package csd.api.modules.trading;
package csd.api.tables;

import java.util.List;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    private double bid = -1;
    private double ask = -1;
    private String now = LocalDateTime.now().toString();
}

