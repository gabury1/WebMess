package code.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;  
import lombok.RequiredArgsConstructor;

@Controller @RequestMapping("/private")
@RequiredArgsConstructor
public class PrivateController 
{

    @RequestMapping("/")
    public String container()
    {

        return "/private/private_container";
    }

    @RequestMapping("/friend")
    public String friend()
    {

        return "/private/friend";
    }

    @RequestMapping("/chat")
    public String chat()
    {

        return "/private/chat";
    }

}
