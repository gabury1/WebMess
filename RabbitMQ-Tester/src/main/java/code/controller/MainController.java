package code.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController
{
    @RequestMapping("/")
    @ResponseBody
    public String home()
    {

        return "이야아아 여기가 홈인가??";
    }

}
