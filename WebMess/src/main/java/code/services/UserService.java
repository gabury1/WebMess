package code.services;

import java.util.HashMap;
import java.util.Optional;

import org.apache.catalina.User;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import code.domain.user.UserEntity;
import code.domain.user.UserRelationEntity;
import code.domain.user.UserRelationRepository;
import code.domain.user.UserRepository;
import code.dto.SignUpDto;
import code.dto.UserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserRelationRepository userRelationRepository;

    @Autowired
    PasswordEncoder encoder;

    ////////////////////////
    //// User Mangement ////
    ////////////////////////
    
    // Create
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

    // Read
    public JSONObject getUserDetail(Long userNo)
    {
        // 익명 유저라면 번호를 -1로 지정해준다.
        UserDto nowUser = getNowUser().orElseGet(() -> UserDto.builder().no(-1L).build());

        HashMap<String, Object> map = new HashMap<>();

        try{
            UserEntity user = userRepository.findByUserNo(userNo).orElseThrow(Exception::new);

            map.put("userNo", user.getUserNo());
            map.put("name", user.getName());
            map.put("introduce", user.getIntroduce());
            map.put("colorCode", user.getColorCode());
            map.put("colorName", user.getColorName());
            map.put("email", user.getEmail());

            // 현재 로그인한 본인인지를 나타냄.
            map.put("isOwner", nowUser.getNo().equals(userNo));

            JSONObject object = new JSONObject(map);
            return object;

        }catch(Exception e)
        {
            System.out.println(e.getLocalizedMessage());
            return new JSONObject();
        }

    }

    // Update

    // Delete


    ///////////////////////
    //// User Relation ////
    ///////////////////////

    // Create+Delete
    public String handleRelation(Long mainNo, Long subNo)
    {
        if(mainNo.equals(subNo))
        {
            return "본인과 친구가 되실 수는 없습니다.";
        }

        try{
            UserEntity sub = userRepository.findByUserNo(subNo).orElseThrow(() -> new Exception("유저를 찾을 수 없음"));

            // 이미 관계가 있다면 삭제, 없다면 생성
            Optional<UserRelationEntity> optional = userRelationRepository.findByMainNoAndSubNo(mainNo, subNo);
            
            if(optional.isPresent())
            {
                userRelationRepository.delete(optional.get());
                return "notFriend";
            }
            else
            {
                // 저장할 엔티티 생성
                UserRelationEntity entity = UserRelationEntity.builder()
                                                              .main(UserEntity.builder().userNo(mainNo).build())
                                                              .sub(sub)
                                                              .build();
                userRelationRepository.save(entity);
                return "okFriend";
            }

        }
        catch(Exception e)
        {
            System.out.println(e.getLocalizedMessage()) ;
            return "fail";
        }

    }

    // Read
    public String isMyFriend(Long mainNo, Long subNo)
    {
        Optional<UserRelationEntity> optional = userRelationRepository.findByMainNoAndSubNo(mainNo, subNo);

        if(optional.isPresent())
        {
            return "true";
        }
        else return "false";

    }

    // 현재 로그인한 유저의 정보를 반환. 없으면 null
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
