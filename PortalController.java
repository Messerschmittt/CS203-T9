public class PortalController {
    private User user;
    public PortalController(){

    }
      // transfer 
    public void transfer(Long fromAccNumber, Long toAccNumber, double amount) throws Exception {
        BankAccount fromAccount = User.retrieveAccount(fromAccNumber);
        BankAccount toAccount = User.retrieveAccount(toAccNumber);

        double balance = fromAccount.getbalance();

        if (balance >= amount) {
            fromAccount.deduct(amount);
            toAccount.add(amount);
        } else {
            throw new Exception("Insufficient funds.");
        }
    }
}
