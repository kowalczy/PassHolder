package pass.passholder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pass.passholder.entity.Account;
import pass.passholder.repos.AccountRepo;

import java.util.Optional;

@Service
public class AccountService {
    private AccountRepo accountRepo;

    @Autowired
    public AccountService(AccountRepo accountRepo){
        this.accountRepo = accountRepo;
    }

    public Iterable<Account> findAll(){
        return accountRepo.findAll();
    }

    public Account findById(int id) throws AccountNotFoundException{
        Optional<Account> result = accountRepo.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        throw new AccountNotFoundException("Could not find Account with id: " + id);
    }

    public Account save(Account account){
        return accountRepo.save(account);
    }

    public void deleteById(int id) throws AccountNotFoundException {
        Optional<Account> result = accountRepo.findById(id);
        if(result.isPresent()){
            accountRepo.deleteById(id);
        }else{
            throw new AccountNotFoundException("Could not find Account with id: " + id);
        }
    }
}
