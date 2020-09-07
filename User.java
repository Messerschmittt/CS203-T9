import java.util.*;

public class User{
    private long userid;        //unique id
    private String username;
    private String password;
    private int age;
    private Date birthDate;  //????
    private String employeeInCharge;

    //key would eb bankAccount, value is unique userid
    private HashMap<BankAccount, Long> bankAcc = new HashMap<BankAccount, Long>();
    

    public User(){
        
    }

    //create new user
    public User(long userid, String username, String password, int age, Date birthDate, String employeeInCharge) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.age = age;
        this.birthDate = birthDate;
        this.employeeInCharge = employeeInCharge;
    }

    //user login
    public User(long userid, String username, String password){
        this.userid = userid;
        this.username = username;
        this.password = password;
    }

    //get userid
    public long getUserid(){
        return userid;
    }

    //get username
    public String getUsername(){
        return username;
    }

    //get password
    public String getPassword(){
        return password;
    }

    //get Age
    public int getAge(){        //should we calculate age ??
        return age;
    }

    //get birthdate
    public Date getBirthDate(){  //is timestamp?? or Date??
        return birthDate;
    }
    
    //get employee in charge
    public String getEmployeeInCharge(){
        return employeeInCharge;
    }

    //set the username
    public void setUsername(String username){
        this.username = username;
    }
    
    //set the password
    public void setPassword(String password){
        this.password = password;
    }

    //set birthdate
    public void setBirthDate(Date birthDate){
        this.birthDate = birthDate;
    }

    //set employee in charge
    public void setEmployeeInCharge(String employeeInCharge){
        this.employeeInCharge = employeeInCharge;
    } 

    //add bank account
    public void addBankAccount(BankAccount acc){
        bankAcc.put(acc, userid);
    }

}