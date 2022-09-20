package code.controller.MessageController;

import java.security.Principal;
import java.util.Map;

import org.json.simple.parser.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.mysql.cj.Session;

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

    @MessageMapping("/friend")
    public void friend(String json, Principal principal)
    {
        
        try {
            JSONObject object;
            object = (JSONObject) parser.parse(json);

            String method = (String) object.get("method");
            String target = (String) object.get("name");
            
            String myName = userService.getNowUser(principal).map((u) -> u.getName()).orElse("anon");
    
            if(method.equals("add"))
            {
    
                sessionStorage.addFriend(myName, target);
            }
            else
            {
    
                sessionStorage.removeFriend(myName, target);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    // 유저리스트를 송신해준다.
    @Scheduled(fixedDelay = 500)
    public void userList()
    {
        JSONObject json = sessionStorage.authenticUserList();

        sender.convertAndSend("/topic/userList", json.toJSONString());
    }
    

}
