package model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=false)
@DynamicUpdate
public class ChannelUser implements Model<Long> {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;

    @NonNull
    private final String userId;
    @NonNull
    private final String userName;
    private final boolean adminRole;
    @NonNull
    @OneToOne
    private final Score score;
    private byte[] avatar;
}