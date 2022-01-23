package pass.passholder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pass.passholder.entity.Account;
import pass.passholder.entity.AppUser;
import pass.passholder.service.AESUtil;
import pass.passholder.service.AccountNotFoundException;
import pass.passholder.service.AccountService;
import pass.passholder.service.UserService;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Controller
public class AccountController {
    @Autowired
    private AccountService accountService;
    private UserService userService;
    private AESUtil aesUtil;

    @Autowired
    public AccountController(AccountService accountService, UserService userService, AESUtil aesUtil) {
        this.accountService = accountService;
        this.userService = userService;
        this.aesUtil = aesUtil;
    }

    @GetMapping("")
    public String showHomePage(){
        return "index";
    }

    @GetMapping("/accounts")
    public String getAll(Model model, Principal principal){
        List<Account> accountList = findAllByUser(userService.findUserByUsername(principal.getName()));
        model.addAttribute("accountList", accountList);
        return "accounts";
    }

    @GetMapping("/accounts/new")
    public String showNewCarForm(Model model){
        model.addAttribute("account", new Account());
        model.addAttribute("pageTitle", "Add new Account");
        return "addNewAccount";
    }

    @PostMapping("/accounts/save")
    public String saveAccount(Account account, RedirectAttributes redirectAttributes, Principal principal){
        redirectAttributes.addFlashAttribute("message", "The Account has been saved succesfully.");
        AppUser user = userService.findUserByUsername(principal.getName());
        if(account.getAppUser() == null)
            account.setAppUser(user);
        try {
            account.setPassword(aesUtil.encrypt(account.getPassword(), user.getUsername()));
            account.setLogin(aesUtil.encrypt(account.getLogin(), user.getUsername()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        accountService.save(account);
        return "redirect:/accounts";
    }

    @GetMapping("/accounts/{id}")
    Account getById(@PathVariable int id){
        try{
            return accountService.findById(id);
        }catch (AccountNotFoundException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @GetMapping("/accounts/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes){
        try {
            Account account = accountService.findById(id);
            model.addAttribute("account", account);
            model.addAttribute("pageTitle", "Your Account");
            return "addNewAccount";
        } catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/accounts";
        }
    }

    @GetMapping("/accounts/show/{id}")
    public String showShowForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes, Principal principal){
        try {
            Account account = accountService.findById(id);
            model.addAttribute("account", account);
            model.addAttribute("aesUtil", aesUtil);
            model.addAttribute("username", principal.getName());
            return "show";
        } catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/accounts";
        }
    }

    @GetMapping("/accounts/delete/{id}")
    public String deleteAccount(@PathVariable int id, RedirectAttributes redirectAttributes){
        try {
            Account account = accountService.findById(id);
            accountService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The Account has been deleted succesfully.");
        } catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/accounts";
    }

    private List<Account> findAllByUser(AppUser appUser){
        List<Account> accountList = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            if(account.getAppUser() == appUser){
                accountList.add(account);
            }
        }
        return accountList;
    }
}
