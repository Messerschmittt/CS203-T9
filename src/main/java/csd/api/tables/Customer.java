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
    private long id;
    
    // private String username;
    // private String password;

    private String full_name;
    private String nric;
    private String phone;
    private String address;
    // private String authorities;
    private String active;

    //
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Account> accounts;


    @OneToOne
    @JoinColumn(name = "customer")
    private Portfolio portfolio;

    // //
    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    // private List<Assests> assests;
    
    @OneToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser application_user;


    // public Customer(String full_name, String authorities, String username, String password){
    //     this.full_name = full_name;
    //     this.authorities = authorities;
    //     this.username = username;
    //     this.password = password;
    // }

    public Customer(ApplicationUser application_user, String full_name) {
        this.application_user = application_user;
        this.full_name = full_name;

    }

    
}