package code.config.websocketSTOMP;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.WebSocketContainer;

import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class HandShaker extends HttpSessionHandshakeInterceptor
{
    public Integer cnt = 0;

    

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, 
                                    Map<String, Object> attributes) throws Exception 
    {
        
        if (request instanceof ServletServerHttpRequest) 
        {
            cnt++;
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(true);

            if (session != null) 
            {
                attributes.put("HTTPSESSIONID", session.getId());
            }
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               WebSocketHandler wsHandler, Exception ex)
    {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        HttpSession session = servletRequest.getServletRequest().getSession(true);
    }

    @EventListener
    public void onConnectEvent(SessionConnectEvent event)
    {
        System.out.println("세션 연결");
        System.out.println(event.getMessage().getHeaders().get("simpSessionId"));
    }

    @EventListener
    public void onSubscribeEvent(SessionSubscribeEvent event)
    {
        System.out.println("구독 연결");
        System.out.println(event);
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event)
    {
        System.out.println("세션 해제");
        System.out.println(event);
    }
    
    @EventListener
    public void onUnSubscribeEvent(SessionUnsubscribeEvent event)
    {
        System.out.println("구독 해제");
        System.out.println(event);
    }

}
