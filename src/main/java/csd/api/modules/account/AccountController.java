package csd.api.modules.account;

import csd.api.tables.*;

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

    public AccountController(AccountRepository accounts, TransRepository transfers){
        this.accounts = accounts;
        this.transfers = transfers;
    }

    
    @GetMapping("/accounts")
    public List<Account> getAccounts(){
        return accounts.findAll();
    }

    @GetMapping("/transfers")
    public List<Trans> getTransactions(){
        return transfers.findAll();
    }

    @PostMapping("/account/createAccount")
    public Account createAccount(@RequestBody Account newAcc){
        return accounts.save(newAcc);
        
    }

    /**
     * Allows users to make a transfer between accounts
     * @param newTrans
     * @return the successful transaction object
     */
    @PostMapping("/transfer/makeTransfer")
    public Trans makeTransaction(@RequestBody Trans newTrans){
        if(accounts.findById(newTrans.getFrom_acc()).isEmpty()){
            throw new AccountNotFoundException(newTrans.getFrom_acc());
        }

        if(accounts.findById(newTrans.getTo_acc()).isEmpty()){
            throw new AccountNotFoundException(newTrans.getTo_acc());
        }

        Account from_acc = accounts.findById(newTrans.getFrom_acc()).get();
        Account to_acc = accounts.findById(newTrans.getTo_acc()).get();
        double amt = newTrans.getAmount();
        if(amt > from_acc.getAvailable_balance()){
            throw new ExceedAvailableBalanceException(from_acc.getId());
        }

        from_acc.setBalance(from_acc.getBalance() - amt);
        to_acc.setBalance(to_acc.getBalance() + amt);

        accounts.save(from_acc);
        accounts.save(to_acc);

        return transfers.save(newTrans);
    }

    
}