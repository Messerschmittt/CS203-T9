package csd.api.tables;

import java.util.List;
import javax.persistence.*;

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
public class Employee {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    //
    @OneToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser application_user;
    
    // private String username;
    // private String password;

    private String full_name;
    // private String authorities;
    
    
    // public Employee(String full_name, String authorities, String username, String password){
    //     this.full_name = full_name;
    //     this.authorities = authorities;
    //     this.username = username;
    //     this.password = password;
    // }
    
    public Employee(ApplicationUser application_user, String full_name) {
        this.application_user = application_user;
        this.full_name = full_name;
    }
}