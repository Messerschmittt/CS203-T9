package csd.api.modules.account;

import csd.api.tables.*;
import csd.api.tables.templates.*;
import csd.api.modules.user.*;


import java.util.List;
import java.util.Optional;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    private AccountRepository accounts;
    private TransRepository transfers;
    private CustomerRepository customers;
    private UserRepository users;

    public AccountController(AccountRepository accounts, TransRepository transfers, CustomerRepository customers, UserRepository users){
        this.accounts = accounts;
        this.transfers = transfers;
        this.users = users;
        this.customers = customers;
    }

    
    @GetMapping("/accounts")
    public List<Account> getAccounts(){
        return accounts.findAll();
    }

    @GetMapping("/transfers")
    public List<Trans> getTransactions(){
        return transfers.findAll();
    }

    // @PostMapping("/account/createAccount")
    // public Account createAccount(@RequestBody Account newAcc){
    //     return accounts.save(newAcc);
        
    // }

    @PostMapping("/account/createAccount")
    public Account createAccount(@RequestBody AccountRecord accountRecord){
        String username = accountRecord.getUsername();
        if (!customers.existsByUsername(username)) {
            throw new CustomerNotFoundException(username);
        }


        Account newAcc = new Account(customers.findByUsername(username)
                        , accountRecord.getBalance(), accountRecord.getAvailable_balance());
        return accounts.save(newAcc);
        
    }


    /**
     * Should not allow the post mapping for this actually
     */
    @PostMapping("/transfer/makeTransfer")
    public Trans makeTransaction(@RequestBody Trans newTrans){
        System.out.println("In make transaction");
    
        // if(accounts.findById(newTrans.getFrom_acc()).isEmpty()){
        //     throw new AccountNotFoundException(newTrans.getFrom_acc());
        // }else if(accounts.findById(newTrans.getTo_acc()).isEmpty()){
        //     throw new AccountNotFoundException(newTrans.getTo_acc());
        // }else{
        //     System.out.println("both valid account");
        // }

        Account from_acc = newTrans.getFrom_account();
        Account to_acc = newTrans.getTo_account();

        double amt = newTrans.getAmount();
        if(amt > from_acc.getAvailable_balance()){
            throw new ExceedAvailableBalanceException(from_acc.getId());
        }

        System.out.println("PRIOR from_acc - B: " + from_acc.getBalance() + " AB: " + from_acc.getAvailable_balance());
        System.out.println("PRIOR to_acc - B: " + to_acc.getBalance() + " AB: " + to_acc.getAvailable_balance());

        from_acc.setBalance(from_acc.getBalance() - amt);
        from_acc.setAvailable_balance(from_acc.getAvailable_balance() - amt);
        to_acc.setBalance(to_acc.getBalance() + amt);
        to_acc.setAvailable_balance(to_acc.getAvailable_balance() + amt);

        System.out.println("AFTER from_acc - B: " + from_acc.getBalance() + " AB: " + from_acc.getAvailable_balance());
        System.out.println("AFTER to_acc - B: " + to_acc.getBalance() + " AB: " + to_acc.getAvailable_balance());

        accounts.save(from_acc);
        accounts.save(to_acc);

        System.out.println("Transaction Completed");

        return transfers.save(newTrans);
    }

    
}