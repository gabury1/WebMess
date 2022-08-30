package code.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller @RequestMapping("/user")
@RequiredArgsConstructor
public class UserController
{
    @RequestMapping("/signup")
    public String signup()
    {
        return "/user/signup";
    }

}
