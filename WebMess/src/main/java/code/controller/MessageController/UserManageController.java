package code.controller.MessageController;

import java.security.Principal;

import org.json.simple.parser.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import code.config.stomp.SessionStorage;
import code.services.UserService;

@Controller
@EnableScheduling
public class UserManageController 
{

    @Autowired
    UserService userService;
    @Autowired
    SessionStorage sessionStorage;
    @Autowired
    SimpMessageSendingOperations sender;

    // 제이슨 파서
    final JSONParser parser = new JSONParser();


    @MessageMapping("/status")
    @SendToUser("/topic/status")
    public String status(String name)
    {

        return sessionStorage.isOnline(name).toString();
    }

    // 유저리스트를 송신해준다.
    @Scheduled(fixedDelay = 500)
    public void userList()
    {
        JSONObject json = sessionStorage.authenticUserList();

        sender.convertAndSend("/topic/userList", json.toJSONString());
    }

    @Scheduled(fixedDelay = 500)
    public void allUserList()
    {
        JSONObject json = sessionStorage.getAllUser();

        sender.convertAndSend("/topic/allUserList", json.toJSONString());

    }


    

}
