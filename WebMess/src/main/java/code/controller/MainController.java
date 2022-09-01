package code.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController
{
    @RequestMapping("/")
    public String home()
    {
        return "/home";
    }

    @RequestMapping("/chat")
    public String chat()
    {
        return "/chat";
    }


}
