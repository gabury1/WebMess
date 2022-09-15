package code.config.websocket;

import code.domain.user.UserEntity;
import code.domain.user.UserRepository;
import code.dto.UserDto;
import code.dto.WebSocketClient;
import code.services.UserService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding.Anonymous;

import org.apache.commons.collections.map.HashedMap;
import org.ietf.jgss.GSSContext;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.fasterxml.jackson.databind.util.JSONPObject;

import javax.mail.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@EnableScheduling
@Getter @Setter
public class WebSocketHandler extends TextWebSocketHandler
{
    // 세션들을 저장함.
    private final List<WebSocketSession> sessionList = new LinkedList<>();
    // 클라이언트를 WebSocketClient(내가 만듬)에 저장. 여러 세션을 포함할 수 있음.
    private final Map<String, WebSocketClient> clientStorage = new ConcurrentHashMap<>();

    // 유저리스트를 받을 세션들의 리스트
    private final List<WebSocketSession> userListReceivers = new LinkedList<>();
    // 채팅을 받을 세션들의 리스트
    private final List<WebSocketSession> chatReceivers = new LinkedList<>();

    // JSON parser
    private static final JSONParser parser = new JSONParser();
    @Autowired
    UserRepository userRepository;

    //////////////////////
    /// Event Listener ///
    //////////////////////

    @Override
    // 처음으로 세션에 소켓을 연결했을때 발생하는 이벤트
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        // 유저명, 만약 익명 유저라면 anonymous
        String userName = getUserName(session);

        // 세션을 단순히 저장하는 리스트(익명 유저도 저장해준다.)
        sessionList.add(session);

        // 유저 기반으로 저장(WebSocketClient는 유저 정보와 그가 가진 세션을 포함한다.)
        // 익명 유저는 제외된다.
        if(!userName.equals("anonymous")) 
        {
            WebSocketClient client;
            if(clientStorage.containsKey(userName))
            {
                // 이미 클라이언트가 서버에 있다면 그걸 갖고 온다.
                client = clientStorage.get(userName);
                client.getSessions().add(session);
            }
            else
            {
                // DB에서 유저를 찾아와 웹소켓 클라이언트로 만들어준다.
                UserEntity entity = userRepository.findByName(userName).get();
                client = entity.toWebSocketClient();
                client.getSessions().add(session);
                // 클라이언트맵에 저장해준다.
                clientStorage.put(userName, client);
            }

            // 속성 좀 추가해줄거임. 그러면 DB가 부담을 덜겠지.
            Map<String, Object> attr = session.getAttributes();           
            attr.put("no", client.getNo());
            attr.put("name", client.getName());
            attr.put("colorCode", client.getColorCode());
            attr.put("client", client);
            
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage)
    {
        String userName = getUserName(session);
    
        try{

            JSONObject json = (JSONObject)parser.parse(textMessage.getPayload());

            String purpose = (String) json.get("purpose");
            if(purpose.equals("subscribe"))
            {
                // 어떤 정보를 수신하겠다는 선언
                subscribe(session, json);
            }
            else if(purpose.equals("chat"))
            {
                // 전체 채팅
                allChat(session, json);
            }
            else if(purpose.equals("status"))
            {
                // 특정 유저의 온라인/오프라인 상태를 체크
                sendStatus(session, json);
            }
            else if(purpose.equals("friend"))
            {
                // 친구 추가/삭제
                handleFriend(session, json);
            }

        }
        catch(Exception e) {System.out.println(e.getMessage());}            


    }

    @Override
    // 클라이언트가 연결 해제될때 발생하는 이벤트
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        String userName = getUserName(session);

        // 세션리스트에서 세션 삭제
        sessionList.remove(session);

        // 익명 유저가 아니라면 클라이언트에서도 삭제.
        if(!userName.equals("anonymous"))
        {
            List<WebSocketSession> list = clientStorage.get(getUserName(session)).getSessions();

            list.remove(session);
            if(list.isEmpty()) clientStorage.remove(getUserName(session));
        }

        // 구독 목록에서도 빼줘야 함.
        if(userListReceivers.contains(session)) userListReceivers.remove(session);
        if(chatReceivers.contains(session)) chatReceivers.remove(session);

    }

    /////////////////////
    /// Message Event ///
    /////////////////////

    // 특정 정보를 수신하겠다는 선언
    public void subscribe(WebSocketSession session, JSONObject request)
    {
        if(request.get("target").equals("userList")) userListReceivers.add(session);
        if(request.get("target").equals("chat")) chatReceivers.add(session);

    }

    // 채팅 수신
    public void allChat(WebSocketSession session, JSONObject request)
    {
        Map<String, Object> attr = session.getAttributes();

        // 전송할 데이터를 JSON에 담아줘야함.
        // 맵에 담아서 보내주는게 JSONObject 생성자에 넘겨줌.
        Map<String, Object> m = new HashMap<>(); 
        m.put("purpose", "chat");
        m.put("sender", attr.get("name"));
        m.put("color", attr.get("colorCode"));
        m.put("content", request.get("content"));
        
        String jsonString =  new JSONObject(m).toJSONString(); 

        // 모든 클라이언트에게 보내준다.
        for(WebSocketSession s : chatReceivers)
        {
            try{
                s.sendMessage(new TextMessage(jsonString)); 
            }
            catch(IOException e){   
            }
        }
    }

    // 유저가 온라인인가 오프라인인가??
    public void sendStatus(WebSocketSession session, JSONObject request)
    {
        HashMap<String, Object> status = new HashMap<>();

        status.put("purpose", "status");
        status.put("user", request.get("target"));

        // 만약 클라이언트 리스트에 유저명이 있다면 online, 아니면 offline
        if(isOnline((String)request.get("target"))) status.put("now", "online");
        else status.put("now", "offline");

        String jsonString = new JSONObject(status).toJSONString();

        try{
            session.sendMessage(new TextMessage(jsonString));
        } catch(Exception e) {System.out.println(e.getLocalizedMessage());}
        
    }

    // 친구 추가/삭제 요청
    public void handleFriend(WebSocketSession session, JSONObject request)
    {
        // 클라이언트 받아오기
        WebSocketClient client = (WebSocketClient)session.getAttributes().get("client");
        List<String> friends = client.getFriends();

        if(request.get("method").equals("add"))
        {
            // 친구명 목록에 이름을 추가해준다.
            friends.add((String) request.get("name"));
            
        }
        else if(request.get("method").equals("delete"))
        {
            // 친구명 목록에서 이름을 삭제해준다.
            friends.add((String) request.get("name"));
        }

    }



    ///////////////////
    //// Scheduled ////
    ///////////////////

    // 유저 정보를 송신해줌.
    @Scheduled(fixedDelay=500)
    public void sendUsers()
    {
        String json = authenticUserList().toString();
        userListReceivers.forEach(v -> {
            try {
                v.sendMessage(new TextMessage(json));

            } catch (IOException e) {
                
                e.printStackTrace();
            }
        });
    }

    ///////////////////
    /////// etc ///////
    ///////////////////
    String getUserName(WebSocketSession session)
    {
        try{

            return session.getPrincipal().getName();
        }
        catch (Exception e)
        {
            return "anonymous";
        }
        
    }

    // 인증된 유저들의 목록과 숫자를 반환해준다.
    JSONObject authenticUserList()
    {
        HashMap<String, Object> userList = new HashMap<>();

        // 유저리스트를 담을 배열
        ArrayList<JSONObject> array = new ArrayList<>();
        for(WebSocketClient s : clientStorage.values())
        {
            // 유저 정보를 담을 해쉬맵
            HashMap<String, Object> info = new HashMap<>();

            info.put("no", s.getNo());
            info.put("name", s.getName());
            info.put("color", s.getColorCode());

            array.add(new JSONObject(info));
        }

        userList.put("purpose", "userList");
        userList.put("userCnt", sessionList.size());
        userList.put("users", array);

        return new JSONObject(userList);
    }

    // 인증된 유저의 친구 목록을 반환해준다.
    JSONObject friendList(WebSocketClient client)
    {
        HashMap<String, Object> list = new HashMap<>();
        List<String> friends = client.getFriends();

        // 유저리스트를 담을 배열
        ArrayList<JSONObject> array = new ArrayList<>();
        for(String s : friends)
        {
            WebSocketClient friend = clientStorage.get(s);

            // 유저 정보를 담을 해쉬맵
            HashMap<String, Object> info = new HashMap<>();

            info.put("no", friend.getNo());
            info.put("name", friend.getName());
            info.put("color", friend.getColorCode());
            info.put("isOnline", isOnline(s));

            array.add(new JSONObject(info));
        }

        list.put("purpose", "userList");
        list.put("userCnt", sessionList.size());
        list.put("users", array);

        return new JSONObject(list);
    }

    Boolean isOnline(String name)
    {
        if(clientStorage.keySet().contains(name)) return true;
        else return false;
    }

}
