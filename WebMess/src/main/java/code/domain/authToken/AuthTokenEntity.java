package code.domain.authToken;

import lombok.*;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="auth_token")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class AuthTokenEntity
{
    @Id @Column(name="token_id", length = 100)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    String tokenId; // 토큰을 식별하는 기본 키

    @Column(name="expire_time")
    LocalDateTime expireTime; // 토큰의 유효기간
    @Column(name="expired")
    Boolean expired; // 토큰의 유효기간이 지났는가?

    @Column(name="authentication")
    Boolean authentication; // 토큰이 인증 완료되었는가?

    @Column(name="target")
    @Enumerated(EnumType.STRING)
    AuthTarget target; // 인증 대상

    @Column(name="email")
    Boolean email; // 인증 대상 메일

}
