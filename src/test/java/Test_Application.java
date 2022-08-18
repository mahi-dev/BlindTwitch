import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import service.AuthenticateTwitchClient;

@SpringBootTest(classes = BlindApplication.class)
public class Test_Application {

    @Autowired
    AuthenticateTwitchClient twichClient;

    @Test
    void shouldAuthenticate(){
        twichClient.getTwitchClient();
    }
}
