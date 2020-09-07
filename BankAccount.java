public class BankAccount{
    private Long Accountnumber;
    private double balance;
    private User user;

    
    public BankAccount(){
        
    }
    
    public BankAccount(Long Accountnumber,double balance, User user){
        this.Accountnumber = Accountnumber;
        this.balance = balance;

    }
    //get user account number
    public Long getaccountnumber(){
        return Accountnumber;
    }
    //get balance
    public double getbalance(){
        return balance;
    }
    // add an amount to the balance
	public void add(double amt) {
		balance += amt;
	}
	// deduct an amount to the balance
	public void deduct(double amt) {
		balance -= amt;
	}


}