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
    
    // private String username;
    // private String password;

    private String full_name;
    private String nric;
    private String phone;
    private String address;
    // private String authorities;
    private String active;

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


    // public Customer(String full_name, String authorities, String username, String password){
    //     this.full_name = full_name;
    //     this.authorities = authorities;
    //     this.username = username;
    //     this.password = password;
    // }

    public Customer(ApplicationUser applicationUser, String full_name) {
        this.applicationUser = applicationUser;
        this.full_name = full_name;

    }

    
}