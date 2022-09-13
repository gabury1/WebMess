package code.controller;

import code.dto.SignUpDto;
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

@Controller @RequestMapping("/user")
@RequiredArgsConstructor
public class UserController
{
    @Autowired
    UserService userService;

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

}
