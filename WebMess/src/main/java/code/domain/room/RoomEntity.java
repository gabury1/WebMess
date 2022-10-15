package code.domain.room;

import code.domain.user.UserEntity;
import lombok.*;
import net.bytebuddy.utility.nullability.NeverNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity @Table(name="room")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class RoomEntity
{
    @Id @Column(name = "room_id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    Long roomId;

    @JoinColumn(name="member1")
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity member1; // 채팅방에 들어온 첫번째 멤버
    @JoinColumn(name="member2")
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity member2; // 채팅방에 들어온 두번째 멤버

    // 첫째 둘째 모두 방을 삭제했다면 아예 이 엔티티를 삭제한다.(대화내용 포함)
    @Column(name="visible1")
    Boolean visible1; // 첫째 멤버가 방을 삭제했는가?
    @Column(name="visible2")
    Boolean visible2; // 둘째 멤버가 방을 삭제했는가?

}
