package code.services;

import code.domain.user.UserEntity;
import code.domain.user.UserRepository;
import code.dto.SignUpDTO;
import code.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder encoder;
    public String userCreate(SignUpDTO newUser) {
        UserEntity user = newUser.toEntity();
        user.setPassword(encoder.encode(user.getPassword())); // 패스워드를 암호화하여 저장할거임.

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return "이미 존재하는 이름입니다. 다른 이름을 선택해주시겠어요?";
        }

        return "success";
    }

    public Optional<UserDTO> getNowUser() {

        // 1. 인증 객체 호출
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();
        // 2. 인증 정보 객체 호출
        Object principal = authentication.getPrincipal();
        if (principal.equals("anonymousUser")) 
            return Optional.ofNullable(null);
        else 
            return Optional.of((UserDTO)principal);

        }
    }
