package csd.api.tables;




import com.fasterxml.jackson.annotation.*;
 


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


    //for some reason, active is set to false on default when using rest client
    // need to enforce one to one
    // not getting linked with application users either

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
                this(full_name, nric, phone,address, username, null, null);
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
        
        return nric.matches("[STGF]\\d\\d\\d\\d\\d\\d\\d[A-Z]");
    }

    public boolean checkPhone() {
        if (phone == null) {
            return true;
        }

        return phone.matches("[89]\\d\\d\\d\\d\\d\\d\\d");
    }
}