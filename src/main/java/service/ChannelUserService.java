package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.ChannelUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ChannelUserRepository;

import javax.transaction.Transactional;

@RequiredArgsConstructor
public class ChannelUserService implements ServiceClient.UserService {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelUserService.class);

    private final ChannelUserRepository repository;


    @Override
    public boolean isUnknowUser(@NonNull String userId){
        return repository.findByUserId(userId).isEmpty();
    }

    @Override
    @Transactional
    public void createUser(@NonNull String userId,@NonNull String username){
        repository.saveAndFlush(new ChannelUser(userId, username));
    }
}