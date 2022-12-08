package controller;

import configuration.BlindConfiguration;
import lombok.RequiredArgsConstructor;
import model.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import service.ServiceClient;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final BlindConfiguration blindConfiguration;
    private final ServiceClient.TwitchService twitchService;

     @GetMapping("/login")
     public String showLogin() {
      return "login";
     }

     @PostMapping("/login")
     public RedirectView login(@ModelAttribute(name="loginForm") Login login, Model m) {

       if(twitchService.isCredentialValid(login.getProvider(), login.getClientToken())) {
         return new RedirectView("/games");
       }
       m.addAttribute("error", "Incorrect Provider and Client Token");
       return new RedirectView("/");

    }
}