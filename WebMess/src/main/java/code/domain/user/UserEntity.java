package code.domain.user;

import code.domain.message.MessageEntity;
import code.domain.room.RoomEntity;
import code.dto.StompClient;
import code.dto.WebSocketClient;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.LinkedList;
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

    @Column(name="name", unique = true)
    String name; // 유저 이름. 이름이 ID의 역할까지 수행함

    @Column(name="password")
    String password; // 비밀번호(암호화 한다)

    @Column(name="introduce")
    String introduce; // 유저 소개

    @Column(name="color_code")
    String colorCode; // 자기 닉네임이 어떤 색으로 뜰지 정하는거임 컬러코드로 들어옴

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

    @JoinColumn(name="message_no")
    @OneToMany(fetch = FetchType.LAZY)
    List<MessageEntity> messages; // 보냈던 메시지

    @JoinColumn(name = "main_no")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<UserRelationEntity> main; // 본인이 등록한 친구 관계

    @JoinColumn(name = "sub_no")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<UserRelationEntity> sub; // 다른 사람이 본인을 친구로 등록한 관계

    public WebSocketClient toWebSocketClient()
    {

        return WebSocketClient.builder()
               .no(userNo)
               .name(name)
               .colorCode(colorCode)
               .colorName(colorName)
               .email(email)
               .introduce(introduce)
               .sessions(new LinkedList<>())
               .build();
               
    }

    public StompClient toStompClient()
    {

        return StompClient.builder()
               .no(userNo)
               .name(name)
               .colorCode(colorCode)
               .colorName(colorName)
               .email(email)
               .introduce(introduce)
               .sessionIds(new LinkedList<>())
               .build();
               
    }

}
