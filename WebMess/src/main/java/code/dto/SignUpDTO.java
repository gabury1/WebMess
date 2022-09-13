package code.dto;

import code.domain.user.UserEntity;
import lombok.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter @Setter @ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder @EqualsAndHashCode
public class SignUpDto
{
    static final EmailValidator validator = EmailValidator.getInstance();

    @Size(min=2, max=21, message="이름은 2자에서 21자로 지어주세요.")
    @NotEmpty(message="이름을 입력해주세요.")
    String name;

    @Size(min=8, max=21, message="비밀번호는 8자에서 21자로 지어주세요.")
    @NotEmpty(message="비밀번호를 입력해주세요.")
    String pw;
    String repw;
    
    @NotEmpty(message="이메일을 입력해주세요.")
    String email;

    @NotEmpty(message="당신의 색깔을 선택해주시겠어요?")
    String personalColor;

    @Size(max=12, message = "색깔의 이름은 12자 이하로 지어주세요!")
    @NotEmpty(message="당신의 색깔에 이름을 붙여주세요!")
    String colorName;

    // 유효한 이메일인지 확인
    public boolean mailCheck()
    {
        return validator.isValid(email);
    }

    // 비밀번호 재입력을 확인
    public boolean rePwCheck(){ return pw.equals(repw);}

    public UserEntity toEntity()
    {
        return UserEntity.builder()
                .name(name) // 이름(아이디)
                .password(pw) // 비밀번호
                .personalColor(personalColor) // 퍼스널 컬러
                .colorName(colorName) // 색깔 이름
                .introduce("안녕하세요!! " + name + "입니다!") // 유저 소게
                .email(email) // 유저의 이메일
                .build();

    }

}
