package code.controller;

import code.config.stomp.SessionStorage;
import code.dto.SignUpDto;
import code.dto.UserDto;
import code.services.UserService;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.cj.Session;

@Controller @RequestMapping("/user")
@RequiredArgsConstructor
public class UserController
{
    @Autowired
    UserService userService;
    @Autowired
    SessionStorage storage;
    /////////////////////
    ////// Request //////
    /////////////////////

    @RequestMapping("/signup")
    public String signup()
    {
        return "/user/signup";
    }

    @RequestMapping("/{userNo}")
    public String detail()
    {

        return "/user/detail";
    }



    /////////////////////////
    ////////// API //////////
    /////////////////////////


    // USER

    // Create
    @PostMapping("/")
    @ResponseBody
    public String create(@Validated SignUpDto newUser, BindingResult error)
    {
        if(error.hasErrors()) return error.getAllErrors().get(0).getDefaultMessage();

        if(!newUser.mailCheck()) return "잘못된 이메일입니다. 다시 입력해주시겠어요?";
        if(!newUser.rePwCheck()) return "비밀번호 재입력이 잘못되었습니다.";

        return userService.userCreate(newUser);
    }

    // Read
    @GetMapping("/")
    public void read(@Param("userNo") Long userNo, HttpServletResponse response)
    {
        try{
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().print(userService.getUserDetail(userNo));
        }
        catch(Exception e)
        {
            
        }
    }

    // Update

    // Delete


    // USER RELATION

    // Create+Delete
    @PostMapping("/relation")
    @ResponseBody
    public String createRelation(@Param("subNo") Long subNo)
    {
        try{
            Long nowUserNo = userService
            .getNowUser()
            .map(u -> u.getNo())
            .orElseThrow(() -> new Exception("유저를 찾을 수 없습니다."));

            // 관계를 생성했다면 okFriend, 삭제했다면 notFriend
            // 외에는 오류 메시지
            return userService.handleRelation(nowUserNo, subNo);

        }catch(Exception e){return e.getLocalizedMessage();}

    }

    // Read (현재 유저와의 관계를 검색)
    @GetMapping("/relation")
    @ResponseBody
    public String getRelation(@Param("subNo") Long subNo)
    {
        // 만약 요청한 유저가 익명이라면 그냥 -1(있을 수 없는 유저번호) 넣어서 false 반환해줘라.
        Long mainNo = userService.getNowUser().map(u -> u.getNo()).orElse(-1L);
        
        // 맞다면 true, 틀리다면 false
        return userService.isMyFriend(mainNo, subNo);
    }

    // Read2 (해당 유저가 친구로 등록한 모든 유저를 불러온다.)
    @GetMapping("/relationList")
    @ResponseBody
    public String getRelationList()
    {
        return storage.getFriendList(userService.getNowUser().map(u -> u.getNo()).orElse(-1L));
    }


}
