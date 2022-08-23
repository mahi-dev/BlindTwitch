package service;

import model.ChannelUser;
import model.Score;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import repository.ChannelUserRepository;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(SpringExtension.class)
class ScoreManagerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ScoreManagerTest.class);

    @MockBean
    ChannelUserRepository repository;
    @Mock
    ChannelUserService userService;

    @InjectMocks
    ScoreManager scoreManager;

    Score score = new Score(BigDecimal.ONE, BigDecimal.ONE);

    ChannelUser user = new ChannelUser("1","mockName",false,score);

    @BeforeEach
    void setUp() throws ServiceClient.Exception {
        given(repository.findById(any(Long.class))).willReturn(java.util.Optional.of(user));
        given(userService.getScore(any(String.class))).willReturn(score);
        given(userService.getUser(any(String.class))).willReturn(user);
        given(userService.getUsers()).willReturn(Set.of(user));
    }

    @AfterEach
    void tearDown() {
         score = new Score(BigDecimal.ONE, BigDecimal.ONE);
         user = new ChannelUser("1","mockName",false,score);
    }

    @Test
    void addToScore() throws ServiceClient.Exception {
        scoreManager.addToScore("1", BigDecimal.ONE);
        LOG.info("USER {}", userService.getUser("1"));
        Assertions.assertEquals(BigDecimal.valueOf(2), userService.getUser("1").getScore().getActualScore());
        Assertions.assertEquals(BigDecimal.valueOf(2), userService.getUser("1").getScore().getGeneralScore());
    }

    @Test
    void removeFromScore() throws ServiceClient.Exception {
        scoreManager.removeFromScore("1", BigDecimal.ONE);
        LOG.info("USER {}", userService.getUser("1"));
        Assertions.assertEquals(BigDecimal.ZERO, userService.getUser("1").getScore().getActualScore());
        Assertions.assertEquals(BigDecimal.ZERO, userService.getUser("1").getScore().getGeneralScore());
    }

    @Test
    void getActualScore() throws ServiceClient.Exception {
        Assertions.assertEquals(BigDecimal.ONE, userService.getUser("1").getScore().getActualScore());
    }

    @Test
    void getGeneralScore() throws ServiceClient.Exception {
        Assertions.assertEquals(BigDecimal.ONE, userService.getUser("1").getScore().getGeneralScore());
    }

    @Test
    void resetAllActualScore() throws ServiceClient.Exception {
        scoreManager.resetAllActualScore();
        Assertions.assertEquals(BigDecimal.ZERO, userService.getUser("1").getScore().getActualScore());
    }

    @Test
    void resetAllGeneralScore() throws ServiceClient.Exception {
        scoreManager.resetAllGeneralScore();
        Assertions.assertEquals(BigDecimal.ZERO, userService.getUser("1").getScore().getGeneralScore());
    }

    @Test
    void resetActualScore() throws ServiceClient.Exception {
        scoreManager.resetActualScore("1");
        Assertions.assertEquals(BigDecimal.ZERO, userService.getUser("1").getScore().getActualScore());
    }

    @Test
    void resetGeneralScore() throws ServiceClient.Exception {
        scoreManager.resetGeneralScore("1");
        Assertions.assertEquals(BigDecimal.ZERO, userService.getUser("1").getScore().getGeneralScore());
    }
}