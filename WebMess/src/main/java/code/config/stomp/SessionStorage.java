package code.config.stomp;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.soap.SOAPBinding.Style;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import code.domain.user.UserEntity;
import code.domain.user.UserRelationEntity;
import code.domain.user.UserRelationRepository;
import code.domain.user.UserRepository;
import code.dto.StompClient;
import code.dto.UserDto;
import code.services.UserService;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
public class SessionStorage
{
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRelationRepository userRelationRepository;

    @Autowired
    UserService userService;

    WebSocketSession session;

    // 유저명 기반으로 Stomp클라이언트를 만든다.(Stomp클라이언트는 자신의 세션 정보들을 포함하고 있음.)
    final Map<String, StompClient> clientMap = new ConcurrentHashMap<>();
    // 세션ID 저장
    final List<String> sessionIdList = new LinkedList<>();

    // 친구리스트을 받을 유저들의 맵 (구독 요청을 넣을때, 이벤트 함수가 두번씩 실행되는 버그가 있다. 해결법을 몰루겠다..)
    final Map<String, Set<String>> friendReceivers = new ConcurrentHashMap<>();

    ////////////////////////////
    ////// Session Manage //////
    ////////////////////////////

    // 연결 성사 시에 호출되는 매서드
    @EventListener
    public void onConnection(SessionConnectEvent event)
    {
        String sessionId = getSessionId(event);
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
    public void onSubscribe(SessionSubscribeEvent event)
    {
        String userName = userService.getNowUser(event).map(u->u.getName()).orElse("anon");
        String sessionId = getSessionId(event);
        //System.out.println(event);

        if(event.getMessage().getHeaders().get("simpDestination").equals("/user/topic/friendList"))
        {
            
           if(friendReceivers.containsKey(userName))
           {
                Set<String> temp = friendReceivers.get(userName);
                temp.add(sessionId);
           }
           else
           {
                Set<String> sessionSet = new HashSet<>();
                sessionSet.add(sessionId);
                friendReceivers.put(userName, sessionSet);
           }

        }

    }

    @EventListener
    public void onDisConnection(SessionDisconnectEvent event)
    {
        String userName = userService.getNowUser(event).map(u->u.getName()).orElse("anon");
        String sessionId = getSessionId(event);
        sessionIdList.remove(sessionId);
        try{
            UserDto user = userService.getNowUser(event).orElseThrow(() -> new NullPointerException());
            StompClient client = clientMap.get(user.getName());
            List<?> list = client.getSessionIds();
            list.remove(sessionId);

            // 유저의 연결이 모두 끝났다면 그냥 클라이언트를 삭제해도 좋다.
            if(list.isEmpty()) clientMap.remove(user.getName());
            
            if(friendReceivers.containsKey(userName))
            {
                Set<String> set = friendReceivers.get(userName);
                set.remove(sessionId);
                if(set.isEmpty()) friendReceivers.remove(userName);
            }

        }
        catch(Exception e)
        {


        }
        
    }




    /////////////////////////////////
    /////////// User Data ///////////
    /////////////////////////////////

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

    // 친구리스트
    public String getFriendList(Long mainNo)
    {
        List<UserRelationEntity> list = userRelationRepository.findByMainNo(mainNo);
        HashMap<String, Object> map = new HashMap<>();

        JSONArray online = new JSONArray();
        JSONArray offline = new JSONArray();
        for(UserRelationEntity u : list)
        {
            HashMap<String, String> userData = new HashMap<>();
            UserEntity sub = u.getSub();

            userData.put("name", sub.getName());
            userData.put("no", sub.getUserNo().toString());
            userData.put("introduce", sub.getIntroduce());
            userData.put("colorCode", sub.getColorCode());
            userData.put("colorName", sub.getColorName());

            if(isOnline(sub.getName()))
            {
                online.add(new JSONObject(userData));
            } else{
                offline.add(new JSONObject(userData));
            }
            
            map.put("friendCnt", list.size());
            map.put("onlineUser", online);
            map.put("offlineUser", offline);

        }
        
        return  new JSONObject(map).toJSONString()  ;
    }


    // 모든 유저를 리스트로 만들어서 전송한다.
    public JSONObject getAllUser()
    {
        List<UserEntity> users = userRepository.findAll();

        HashMap<String, Object> data = new JSONObject();
        
        JSONArray online = new JSONArray();
        JSONArray offline = new JSONArray();

        for(UserEntity u : users)
        {       
            HashMap<String, String> userData = new HashMap<>();
            userData.put("name", u.getName());
            userData.put("no", u.getUserNo().toString());
            userData.put("introduce", u.getIntroduce());
            userData.put("colorCode", u.getColorCode());
            userData.put("colorName", u.getColorName());

            if(clientMap.containsKey(u.getName())) online.add(new JSONObject(userData));
            else offline.add(new JSONObject(userData));

        }

        
        data.put("userCnt", users.size());
        data.put("onlineUser", online);
        data.put("offlineUser", offline);


        return new JSONObject(data);
    }

    // 유저가 현재 온라인 상태인가?
    public Boolean isOnline(String name)
    {   
        if(clientMap.keySet().contains(name)) return true;
        else return false;
    }

    // 접속의 세션을 반환
    String getSessionId(AbstractSubProtocolEvent event)
    {

        return (String)event.getMessage().getHeaders().get("simpSessionId");
    }

}
