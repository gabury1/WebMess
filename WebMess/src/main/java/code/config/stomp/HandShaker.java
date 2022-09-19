package code.config.stomp;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
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
    @Autowired
    UserService userService;

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
            UserDto user = userService.getNowUser(event).orElseThrow(() -> new NullPointerException());
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
            UserDto user = userService.getNowUser(event).orElseThrow(() -> new NullPointerException());
            StompClient client = clientMap.get(user.getName());
            List<?> list = client.getSessionIds();
            list.remove(sessionId);

            // 유저의 연결이 모두 끝났다면 그냥 클라이언트를 삭제해도 좋다.
            if(list.isEmpty()) clientMap.remove(user.getName());
            
        }
        catch(Exception e)
        {


        }

        
    }

    // 인증된 유저들의 목록과 숫자를 반환해준다.
    public JSONObject authenticUserList()
    {
        HashMap<String, Object> userList = new HashMap<>();

        // 유저리스트를 담을 배열
        ArrayList<JSONObject> array = new ArrayList<>();
        for(StompClient s : clientMap.values())
        {
            // 유저 정보를 담을 해쉬맵
            HashMap<String, Object> info = new HashMap<>();

            info.put("no", s.getNo());
            info.put("name", s.getName());
            info.put("color", s.getColorCode());

            array.add(new JSONObject(info));
        }

        userList.put("purpose", "userList");
        userList.put("userCnt", sessionIdList.size());
        userList.put("users", array);

        return new JSONObject(userList);
    }


}
