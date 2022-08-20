package repository;

import model.ChannelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {
    List<ChannelUser> findByUserId(String userId);
    List<ChannelUser> findByAdminRoleTrue();
}

