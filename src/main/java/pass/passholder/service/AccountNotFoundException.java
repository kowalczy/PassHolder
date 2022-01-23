package pass.passholder.service;

public class AccountNotFoundException extends Throwable{
    public AccountNotFoundException(String mess){
        super(mess);
    }
}
