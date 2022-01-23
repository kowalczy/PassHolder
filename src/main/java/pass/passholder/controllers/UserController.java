package pass.passholder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pass.passholder.entity.AppUser;
import pass.passholder.service.UserService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/sign-up")
    public String signup(Model model){
        model.addAttribute("user", new AppUser());
        return "sign-up";
    }

    @PostMapping("/register")
    public String register(AppUser appUser, RedirectAttributes redirectAttributes){
        if(appUser.getUsername() != "" && appUser.getPassword() != ""){
            for(AppUser user : userService.getAllUsers()){
                if(user.getUsername().equals(appUser.getUsername())){
                    redirectAttributes.addFlashAttribute("message", "Invalid username! There is user with such username already!");
                    return "redirect:/sign-up";
                }
            }
            userService.addUser(appUser);
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("message", "Invalid username or password!");
        return "redirect:/sign-up";
    }

}
