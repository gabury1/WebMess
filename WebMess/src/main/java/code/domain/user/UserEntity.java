package code.domain.user;

import code.domain.message.MessageEntity;
import code.domain.room.RoomEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity @Table(name="user")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class UserEntity
{
    @Id @Column(name="user_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userNo;

    @Column(name="name")
    String name; // 유저 이름. 이름이 ID의 역할까지 수행함

    @Column(name="password")
    String password; // 비밀번호(암호화 한다)

    @Column(name="introduce")
    String introduce; // 유저 소개

    @Column(name="personal_color")
    String personalColor; // 자기 닉네임이 어떤 색으로 뜰지 정하는거임 컬러코드로 들어옴

    @Column(name="color_name")
    String colorName; // 그 색깔에 이름도 붙여줄 수 있음

    @Column(name="email")
    String email; // 이메일 (변경 불가)

    @JoinColumn(name="member1")
    @OneToMany(fetch = FetchType.LAZY)
    List<RoomEntity> rooms1; // 멤버1로 들어가있는 방

    @JoinColumn(name="member2")
    @OneToMany(fetch = FetchType.LAZY)
    List<RoomEntity> rooms2; // 멤버2로 들어가있는 방

    @JoinColumn(name="message")
    @OneToMany(fetch = FetchType.LAZY)
    List<MessageEntity> messages; // 보냈던 메시지

}
