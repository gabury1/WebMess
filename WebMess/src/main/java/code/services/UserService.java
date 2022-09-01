package code.services;

import code.domain.user.UserEntity;
import code.domain.user.UserRepository;
import code.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService
{
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    public String userCreate(SignUpDTO newUser)
    {
        UserEntity user = newUser.toEntity();
        user.setPassword(encoder.encode(user.getPassword())); // 패스워드를 암호화하여 저장할거임.

        try{
            userRepository.save(user);
        }
        catch (Exception e)
        {
            return "이미 존재하는 이름입니다. 다른 이름을 선택해주시겠어요?";
        }

        return "success";
    }


}
