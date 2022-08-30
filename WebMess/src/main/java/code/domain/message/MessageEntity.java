package code.domain.message;

import code.domain.room.RoomEntity;
import code.domain.user.UserEntity;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name="message")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class MessageEntity
{
    @Id @Column(name="message_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long messageNo; // 메시지의 번호

    @Column(name="content")
    String content; // 메시지 내용

    @JoinColumn(name="sender_no")
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity sender; // 메시지 송신자

    @JoinColumn(name="room_no")
    @ManyToOne(fetch = FetchType.LAZY)
    RoomEntity room; // 메시지가 보내진 방

}
