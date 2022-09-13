package code.services;

import code.domain.user.UserEntity;
import code.domain.user.UserRepository;
import code.dto.SignUpDto;
import code.dto.UserDto;
import lombok.RequiredArgsConstructor;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;

    public String userCreate(SignUpDto newUser) {
        UserEntity user = newUser.toEntity();
        user.setPassword(encoder.encode(user.getPassword())); // 패스워드를 암호화하여 저장할거임.

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return "이미 존재하는 이름입니다. 다른 이름을 선택해주시겠어요?";
        }

        return "success";
    }

    public JSONObject getUserDetail(Long userNo)
    {
        JSONObject object = new JSONObject();

        try{
            UserEntity user = userRepository.findByUserNo(userNo).orElseThrow(Exception::new);

            object.put("userNo", user.getUserNo());
            object.put("name", user.getName());
            object.put("introduce", user.getIntroduce());
            object.put("colorCode", user.getColorCode());
            object.put("colorName", user.getColorName());
            object.put("email", user.getEmail());

            return object;

        }catch(Exception e)
        {
            return object;
        }

    }

    public Optional<UserDto> getNowUser() {

        // 1. 인증 객체 호출
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();
        // 2. 인증 정보 객체 호출
        Object principal = authentication.getPrincipal();
        if (principal.equals("anonymousUser")) 
            return Optional.ofNullable(null);
        else 
            return Optional.of((UserDto)principal);

        }
    }
