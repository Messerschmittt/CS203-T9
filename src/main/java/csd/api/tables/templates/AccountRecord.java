package csd.api.tables.templates;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountRecord {
    // class for parsing the json input from the rest client


    private int customer_id;
    private double balance;
    
}