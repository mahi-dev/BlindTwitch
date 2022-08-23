package service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.ChannelUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ScoreManager implements ServiceClient.ScoreService{

    private static final Logger LOG = LoggerFactory.getLogger(ScoreManager.class);

    private final ServiceClient.UserService userService;

    @Override
    public void addToScore(@NonNull String userId, BigDecimal winningPoint) throws ServiceClient.Exception {
        userService.getScore(userId).setGeneralScore(getGeneralScore(userId).add(winningPoint));
        userService.getScore(userId).setActualScore(getActualScore(userId).add(winningPoint));
    }

    @Override
    public void removeFromScore(@NonNull String userId, BigDecimal removePoint) throws ServiceClient.Exception {
        userService.getScore(userId).setGeneralScore(getGeneralScore(userId).subtract(removePoint));
        userService.getScore(userId).setActualScore(getActualScore(userId).subtract(removePoint));
    }

    @Override
    public BigDecimal getActualScore(@NonNull String userId) throws ServiceClient.Exception {
       return userService.getScore(userId).getActualScore();
    }

    @Override
    public BigDecimal getGeneralScore(@NonNull String userId) throws ServiceClient.Exception {
        return userService.getScore(userId).getGeneralScore();
    }

    @Override
    public void resetAllActualScore() throws ServiceClient.Exception {
        userService.getUsers().stream().map(ChannelUser::getScore).forEach(
                s->s.setActualScore(BigDecimal.ZERO));
    }

    @Override
    public void resetAllGeneralScore() throws ServiceClient.Exception {
        userService.getUsers().stream().map(ChannelUser::getScore).forEach(
                s->s.setGeneralScore(BigDecimal.ZERO));
    }

    @Override
    public void resetActualScore(@NonNull String userId) throws ServiceClient.Exception {
        userService.getScore(userId).setActualScore(BigDecimal.ZERO);
    }

    @Override
    public void resetGeneralScore(@NonNull String userId) throws ServiceClient.Exception {
        userService.getScore(userId).setGeneralScore(BigDecimal.ZERO);
    }
}
