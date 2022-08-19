package controller;

import configuration.BlindConfiguration;
import lombok.RequiredArgsConstructor;
import model.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
     public String login(@ModelAttribute(name="loginForm") Login login, Model m) {

       var uname = login.getUsername();
       var pass = login.getPassword();

         var provider = blindConfiguration.provider();
         var clientId = blindConfiguration.clientId();
         var clientSecret = blindConfiguration.clientSecret();
         var clientToken = blindConfiguration.clientToken();
         var channel = blindConfiguration.channel();
         var valid = twitchService.isCredentialValid(blindConfiguration);
       if(uname.equals("Admin") && pass.equals("Admin@123")) {
         m.addAttribute("uname", uname);
         m.addAttribute("pass", pass);
         return "welcome";
       }
       m.addAttribute("error", "Incorrect Username & Password");
       return "login";

    }
}