package service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.ChannelUser;
import model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ChannelUserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChannelUserService implements ServiceClient.UserService {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelUserService.class);

    private final ChannelUserRepository repository;

    @Override
    public Set<ChannelUser> getUsers() {
        return repository.findAll().stream().collect(Collectors.toSet());
    }

    @Override
    public ChannelUser getUser(@NonNull String userId) throws ServiceClient.Exception {
        return repository.findById(Long.valueOf(userId))
                .orElseThrow(()-> new ServiceClient.Exception(String.format("User not Found with id: %s", userId)));
    }

    @Override
    public boolean isUnknowUser(@NonNull String userId){
        return repository.findByUserId(userId).isEmpty();
    }

    @Transactional
    @Override
    public void deleteUser(@NonNull String userId) {
        repository.deleteById(Long.valueOf(userId));
    }

    @Override
    @Transactional
    public void createUser(@NonNull String userId,@NonNull String username, boolean hasAdminRole){
        repository.saveAndFlush(new ChannelUser(userId, username, hasAdminRole, new Score(BigDecimal.ZERO,BigDecimal.ZERO)));
    }

    @Override
    public List<ChannelUser> getAdministratorUsers() {
        return repository.findByAdminRoleTrue();
    }

    @Override
    public Score getScore(@NonNull String userId) throws ServiceClient.Exception {
        return getUser(userId).getScore();
    }

    @Override
    public void update(@NonNull ChannelUser user) {
        repository.saveAndFlush(user);
    }
}