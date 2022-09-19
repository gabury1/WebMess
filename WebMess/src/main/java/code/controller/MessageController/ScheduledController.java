package code.controller.MessageController;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import code.config.stomp.HandShaker;

@Controller
@EnableScheduling
public class ScheduledController 
{

    @Autowired
    HandShaker sessionStorage;
    @Autowired
    SimpMessageSendingOperations sender;

    @Scheduled(fixedDelay = 1000)
    public void userList()
    {
        JSONObject json = sessionStorage.authenticUserList();

        sender.convertAndSend("/sub/userList", json.toJSONString());
    }
    


}
