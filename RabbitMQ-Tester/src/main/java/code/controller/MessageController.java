package code.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import code.config.websocketSTOMP.HandShaker;

@Controller
@RequiredArgsConstructor
@EnableScheduling
public class MessageController
{
    @Autowired
    HandShaker connector;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/hello")
    @SendTo("/sub/test")
    public String message(String message)
    {   
        System.out.println(message);
        return message;
    }
    
    @Scheduled(fixedDelay = 1000)
    public void test()
    {

        simpMessageSendingOperations.convertAndSend("/sub/test", "연결 수 " + connector.cnt);
    }
    
}
