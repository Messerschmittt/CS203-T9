package csd.api.modules.account;

import csd.api.tables.*;
import csd.api.tables.templates.*;
import csd.api.modules.user.*;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

    public AccountController(AccountRepository accounts, TransRepository transfers, CustomerRepository customers, UserRepository users){
        this.accounts = accounts;
        this.transfers = transfers;
        this.customers = customers;
    }

    
    @GetMapping("/accounts")
    public List<Account> getAccounts(Authentication auth){
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            System.out.println("user = " + auth.getName());
            Customer c = customers.findByUsername(auth.getName());
            return accounts.findAllByCustomer_Id(c.getId());
        }

        return accounts.findAll();
    }

    @GetMapping("/accounts/{id}")
    public Account getAccountById(@PathVariable Integer id, Authentication auth){
        Optional<Account> a = accounts.findById(id);
        if(a.isEmpty()){
            throw new AccountNotFoundException(id);
        }
        Account acc = a.get();

        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if(!auth.getName().equals(acc.getCustomer().getUsername())){
                throw new UnauthorisedAccountAccessException(id);
            }
        }

        return acc;
    }


    @GetMapping("/transactions")
    public List<Trans> getTransactions(Authentication auth){
        return transfers.findAll();
    }

    @GetMapping("/accounts/{account_id}/transactions")
    public List<Trans> getTransactionsByAccount(@PathVariable Integer account_id, Authentication auth){
        Optional<Account> a = accounts.findById(account_id);
        if(a.isEmpty()){
            throw new AccountNotFoundException(account_id);
        }
        Account acc = a.get();

        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if(!auth.getName().equals(acc.getCustomer().getUsername())){
                throw new UnauthorisedAccountAccessException(account_id);
            }
        }
        
        return acc.getTransactions_from();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public Account createAccount(@RequestBody AccountRecord accountRecord){
        if (!customers.existsById(accountRecord.getCustomer_id())) {
            throw new CustomerNotFoundException(accountRecord.getCustomer_id());
        }


        Account newAcc = new Account(customers.findById(accountRecord.getCustomer_id()).get()
                        , accountRecord.getBalance(), accountRecord.getBalance());
        return accounts.save(newAcc);
        
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/transactions")
    public Trans makeSimpleTrans(@RequestBody TransferRecord newTransRecord, Authentication auth){
        Optional<Account> f_a = accounts.findById(newTransRecord.getFrom_account());
        Optional<Account> t_a = accounts.findById(newTransRecord.getTo_account());
        if(f_a.isEmpty()){
            throw new AccountNotFoundException(newTransRecord.getFrom_account());
        }
        if(t_a.isEmpty()){
            throw new AccountNotFoundException(newTransRecord.getTo_account());
        }

        Account from_acc = f_a.get();
        Account to_acc = t_a.get();

        if(!auth.getName().equals(from_acc.getCustomer().getUsername())){
            throw new UnauthorisedAccountAccessException(from_acc.getId());
        }

        Trans newTrans = new Trans();
        newTrans.setAmount(newTransRecord.getAmount());
        newTrans.setFrom_account(from_acc);
        newTrans.setTo_account(to_acc);

        double amt = newTrans.getAmount();
        if(amt > from_acc.getAvailable_balance()){
            throw new ExceedAvailableBalanceException(from_acc.getId());
        }

        from_acc.setBalance(from_acc.getBalance() - amt);
        from_acc.setAvailable_balance(from_acc.getAvailable_balance() - amt);
        to_acc.setBalance(to_acc.getBalance() + amt);
        to_acc.setAvailable_balance(to_acc.getAvailable_balance() + amt);

        accounts.save(from_acc);
        accounts.save(to_acc);
    
        return transfers.save(newTrans);
    }
    
    public Trans makeTransaction(@RequestBody Trans newTrans){
        System.out.println("In make transaction");
    
        if(accounts.findById(newTrans.getFrom_account().getId()).isEmpty()){
            throw new AccountNotFoundException(newTrans.getFrom_account().getId());
        }else if(accounts.findById(newTrans.getTo_account().getId()).isEmpty()){
            throw new AccountNotFoundException(newTrans.getTo_account().getId());
        }else{
            System.out.println("both valid account");
        }

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