package code.domain.room;

import code.domain.user.UserEntity;
import lombok.*;
import net.bytebuddy.utility.nullability.NeverNull;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Entity @Table(name="room")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class RoomEntity
{
    @Id @Column(name = "room_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long roomNo;

    @JoinColumn(name="member1")
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity member1; // 채팅방에 들어온 첫번째 멤버
    @JoinColumn(name="member2")
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity member2; // 채팅방에 들어온 두번째 멤버

}
