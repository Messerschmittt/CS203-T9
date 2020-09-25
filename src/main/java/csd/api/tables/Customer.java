package csd.api.tables;

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
    private String nric;
    private String phone;
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
    
    @OneToOne
    @JoinColumn(name = "ApplicationUserId")
    private ApplicationUser applicationUser;


    //for some reason, active is set to false on default when using rest client
    // need to enforce one to one
    // not getting linked with application users either

    public Customer(String full_name, String nric, String phone, String address, 
            String username, String password, String authorities, boolean active) {
                this.full_name = full_name;
                this.nric = nric;
                this.phone = phone;
                this.address = address;
                this.username = username;
                this.password = password;
                this.authorities = authorities;
                this.active = active;
    }

    public Customer(String full_name, String nric, String phone, String address, 
            String username, String password, String authorities) {
                this(full_name, nric, phone,address, username, password, authorities, true);
    }
    
    public Customer(String full_name, String nric, String phone, String address, 
            String username) {
                this(full_name, nric, phone,address, username, null, null);
    }

    public Customer(String full_name, String username) { // can remove

                this(full_name, null, null ,null , username);
    }
    

    // public Customer(ApplicationUser applicationUser, String full_name) {
    //     this.applicationUser = applicationUser;
    //     this.full_name = full_name;

    // }

    
}