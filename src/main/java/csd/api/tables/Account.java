package csd.api.tables;

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
public class Account {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    
    private String username;
    private String password;

    private String full_name;
    private String nric;
    private String phone;
    private String address;
    private String authorities;
    private String active;
    
    public Account(String full_name, String authorities, String username, String password){
        this.full_name = full_name;
        this.authorities = authorities;
        this.username = username;
        this.password = password;
    }

    
}