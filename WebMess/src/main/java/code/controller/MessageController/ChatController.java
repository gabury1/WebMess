package code.controller.MessageController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import code.config.stomp.SessionStorage;
import code.dto.UserDto;
import code.services.UserService;

@Controller
public class ChatController 
{
    //////////////////////////////////
    /// 모든 채팅을 관리하는 컨트롤러
    //////////////////////////////////

    @Autowired
    SessionStorage sessionStorage;
    @Autowired
    UserService userService;


    // 전체 채팅
    @MessageMapping("/allChat")
    @SendTo("/sub/allChat")
    public String allChat(@Payload String data, Principal principal)
    {
        try{
            Map<String, String> map = new HashMap<>();
            UserDto user = userService.getNowUser(principal).orElseThrow(() -> new Exception("인증되지 않은 유저의 채팅"));

            map.put("color", user.getColorCode());
            map.put("sender", user.getName());
            map.put("content", data);

            return new JSONObject(map).toJSONString();

        } catch(Exception e)
        {
            System.out.println("전체 채팅 오류 : " + e.getMessage());
            return null;
        }

    }
    
    
    
}
