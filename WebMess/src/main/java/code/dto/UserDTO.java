package code.dto;

import code.domain.user.UserEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class UserDto implements UserDetails
{
    Long no;            // 번호
    String name;        // 이름
    String password;        // 비밀번호
    String email;           // 이메일
    String introduce;       // 본인 소개

    String colorCode;   // 개인 색깔
    String colorName;       // 색깔 이름

    private Set<GrantedAuthority> authorities;  // 부여된 인증들의 권한

    public UserDto(UserEntity user)
    {
        no = user.getUserNo();
        password = user.getPassword();
        name = user.getName();
        introduce = user.getIntroduce();
        colorCode = user.getColorCode();
        colorName = user.getColorName();
        email = user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return null;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
