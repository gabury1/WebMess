package code.config.stomp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import code.domain.user.UserEntity;
import code.domain.user.UserRepository;
import code.dto.StompClient;
import code.dto.UserDto;
import code.services.UserService;

public class HandShaker extends HttpSessionHandshakeInterceptor
{
    @Autowired
    UserRepository userRepository;


    // 유저명 기반으로 Stomp클라이언트를 만든다.(Stomp클라이언트는 자신의 세션 정보들을 포함하고 있음.)
    final Map<String, StompClient> clientMap = new ConcurrentHashMap<>();
    // 세션ID 저장
    final List<String> sessionIdList = new LinkedList<>();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, 
                                    Map<String, Object> attributes) throws Exception 
    {
        
        if (request instanceof ServletServerHttpRequest) 
        {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);

            if (session != null) {
                attributes.put("HTTPSESSIONID", session.getId());
            }
        }
        return true;
    }

    @EventListener
    public void onConnection(SessionConnectEvent event)
    {
        String sessionId = (String)event.getMessage().getHeaders().get("simpSessionId");
        sessionIdList.add(sessionId);

        try{
            UserDto user = getNowUser(event).orElseThrow(() -> new NullPointerException());
            StompClient client;
            if(clientMap.containsKey(user.getName()))
            {
                client = clientMap.get(user.getName());
                client.getSessionIds().add(sessionId);
            }
            else
            {
                client = userRepository.findByName(user.getName()).get().toStompClient();
                client.getSessionIds().add(sessionId);
                clientMap.put(user.getName(), client);
            }
            System.out.println(client);

        }
        catch(NullPointerException e)
        {
            return;
        }
        catch(Exception e)
        {
            System.out.println("커넥션 오류 : " + e.getMessage());
            return ;
        }

    }

    @EventListener
    public void onDisConnection(SessionDisconnectEvent event)
    {
        String sessionId = (String)event.getMessage().getHeaders().get("simpSessionId");
        sessionIdList.remove(sessionId);
        try{
            UserDto user = getNowUser(event).orElseThrow(() -> new NullPointerException());
            StompClient client = clientMap.get(user.getName());
            List<?> list = client.getSessionIds();
            list.remove(sessionId);

            if(list.isEmpty())
                clientMap.remove(user.getName());
            
        }
        catch(Exception e)
        {


        }

        
    }


    public Optional<UserDto> getNowUser(AbstractSubProtocolEvent event)
    {
        try{
            UsernamePasswordAuthenticationToken t = (UsernamePasswordAuthenticationToken) event.getUser();
            return Optional.ofNullable((UserDto)t.getPrincipal());
        }
        catch(Exception e)
        {
            return Optional.ofNullable(null);
        }
        
    }


}
