package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import service.AuthenticateTwitchClient;

@Controller
@RequiredArgsConstructor
public class BlindController {

    public final AuthenticateTwitchClient client;

}
