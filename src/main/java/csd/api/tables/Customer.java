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
public class Customer {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    
    private String username;
    private String password;

    private String full_name;
    private String nric;
    private String phone;
    private String address;
    private int age;
    private Date dateOfBirth;  //??
    private String authorities;
    private String active;

    public Customer(String full_name, String authorities, String username, String password){
        this.full_name = full_name;
        this.authorities = authorities;
        this.username = username;
        this.password = password;
    }

    //get userid
    public long getCustomerID(){
        return id;
    }

    //get full_name
    public String getCustomerName(){
        return full_name;
    }

    //get password
    public String getPassword(){
        return password;
    }

    //get Age
    public int getAge(){        //should we calculate age / input from user??
        return age;
    }

    //get date of birth
    public Date getDateOfBirth(){
        return dateOfBirth;
    }
    
    //get customer's authorities        //do we need??
    public String getAuthorities(){
        return authorities;
    }

    //set the username
    public void setUsername(String username){
        this.username = username;
    }
    
    //set the password
    public void setPassword(String password){
        this.password = password;
    }

    //set date of birth
    public void setDateOfBirth(Date dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    //set the age
    public void setAge(int age){
        this.age = age;
    }
    
    //set authorities   //still need??
    public void setAuthorities(String authorities){
        this.authorities = authorities;
    } 

}