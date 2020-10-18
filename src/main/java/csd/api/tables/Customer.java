package csd.api.tables;




import com.fasterxml.jackson.annotation.*;
import javax.validation.constraints.Pattern;
 

import java.lang.Integer;

import java.util.List;
import javax.persistence.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.sound.sampled.Port;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class Customer {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    private String full_name;
    private String nric = null;

    // @Pattern(message = "Invalid phone number.", regexp = "[89]\\d\\d\\d\\d\\d\\d\\d")
    private String phone = null;
    private String address;
    private String username;
    private String password;
    private String authorities;
    private boolean active;

    //
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Account> accounts;


    @OneToOne(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Portfolio portfolio;

    //
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Assets> assets;
    
    // @OneToOne(fetch = FetchType.LAZY, optional = false)
    // @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    // @JsonIdentityReference(alwaysAsId = true)
    // @JsonProperty("application_User_id")
    // @JoinColumn(name = "application_User_id")
    @OneToOne
    // to deal with huge errors
    
    @JsonIgnore

    private ApplicationUser application_User;




    public Customer(String full_name, String nric, String phone, String address, 
            String username, String password, String authorities, boolean active, Portfolio portfolio) {
                this.full_name = full_name;
                this.nric = nric;
                this.phone = phone;
                this.address = address;
                this.username = username;
                this.password = password;
                this.authorities = authorities;
                this.active = active;
                this.portfolio = portfolio;
    }

    public Customer(String full_name, String nric, String phone, String address, 
            String username, String password, String authorities) {
                this(full_name, nric, phone,address, username, password, authorities, true, null);
    }
    
    
    public Customer(String full_name, String nric, String phone, String address, 
            String username) {
                this(full_name, nric, phone,address, username, null, null); // do we need this?
    }

    public Customer(String full_name, String username) { // can remove

                this(full_name, null, null ,null , username);
    }

//     public Customer(String full_name, String username, Portfolio portfolio) { // for application.java

//         this(full_name, null, null ,null , username, null, null, true, portfolio);
// }


    

    public Customer(ApplicationUser application_User, String full_name) {
        this.application_User = application_User;
        this.full_name = full_name;
        this.password = application_User.getPassword();
        this.username = application_User.getUsername();
        this.authorities = application_User.getSimpleAuthorities();
        this.active = true;
    }

    // Checks if NRIC input is valid, 
    // if input is null, then it is considered valid, 
    public boolean checkNRIC() {
        if (nric == null) {
            return true;
        }

        nric = nric.toUpperCase();
        
        if (!nric.matches("[STGF]\\d\\d\\d\\d\\d\\d\\d[A-Z]")) {
            return false;
        }

        int num = Integer.parseInt(nric.substring(1, 8));
        int checksum = 0;
        int temp = 0;

        for (int i = 2 ; i < 8 ; i++) {
            temp = num % 10;
            checksum += temp * i;
            num /= 10;
        }

        temp = num % 10;
        checksum += temp * 2;
        num /= 10;

        if (nric.charAt(0) == 'T' || nric.charAt(0) == 'G' ) {
            checksum += 4;
        }

        checksum %= 11;

        char last  = 'S';

        if (nric.charAt(0) == 'T' || nric.charAt(0) == 'S') {
            switch (checksum) {
                case 0:
                    last = 'J';
                    break;
                case 1:
                    last = 'Z';
                    break;
                case 2:
                    last = 'I';
                    break;
                case 3:
                    last = 'H';
                    break;
                case 4:
                    last = 'G';
                    break;
                case 5:
                    last = 'F';
                    break;
                case 6:
                    last = 'E';
                    break;
                case 7:
                    last = 'D';
                    break;
                case 8:
                    last = 'C';
                    break;
                case 9:
                    last = 'B';
                    break;
                case 10:
                    last = 'A';
                    break;
            }
        } else {
            switch (checksum) {
                case 0:
                    last = 'X';
                    break;
                case 1:
                    last = 'W';
                    break;
                case 2:
                    last = 'U';
                    break;
                case 3:
                    last = 'T';
                    break;
                case 4:
                    last = 'R';
                    break;
                case 5:
                    last = 'Q';
                    break;
                case 6:
                    last = 'P';
                    break;
                case 7:
                    last = 'N';
                    break;
                case 8:
                    last = 'M';
                    break;
                case 9:
                    last = 'L';
                    break;
                case 10:
                    last = 'K';
                    break;
            }
        }
        
        return nric.charAt(8) == last;
    }

    public boolean checkPhone() {
        if (phone == null) {
            return true;
        }

        return phone.matches("[89]\\d\\d\\d\\d\\d\\d\\d");
    }
}