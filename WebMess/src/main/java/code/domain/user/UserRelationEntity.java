package code.domain.user;

import lombok.*;

import javax.persistence.*;

@Entity @Table(name="user_relation")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class UserRelationEntity
{
    // 친구 추가는 일방적으로 이루어짐.

    @Id @Column(name="user_relation_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long UserRelationNo;

    @JoinColumn(name="main_no")
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity main; // 누가

    @JoinColumn(name="sub_no")
    @ManyToOne(fetch = FetchType.EAGER)
    UserEntity sub; // 누구를

}
