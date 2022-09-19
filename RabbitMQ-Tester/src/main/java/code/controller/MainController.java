package code.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MainController
{

    @GetMapping("/chat")
    public String chatGET(){

        return "chat";
    }

    @RequestMapping("/test")
    public String test()
    {

        return "STOMPtest";
    }

}
