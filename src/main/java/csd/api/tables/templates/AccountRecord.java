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


    private String username;
    private double balance;
    private double available_balance;
    
}