package code.config.websocket;

import code.domain.user.UserEntity;
import code.domain.user.UserRepository;
import code.dto.UserDTO;
import code.services.UserService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.apache.tomcat.util.json.JSONParser;
import org.ietf.jgss.GSSContext;
import org.json.JSONArray;
import org.json.JSONObject;
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

import javax.mail.Session;

import java.io.IOException;
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
    // 세션 아이디 기반으로 세션들을 저장함.
    private final Map<String, WebSocketSession> storageBySessionId = new ConcurrentHashMap<>();
    // 유저명 기반으로 세션들을 저장함.
    private final Map<String, List<WebSocketSession>> storageByUserName = new ConcurrentHashMap<>();

    @Autowired
    UserRepository userRepository;

    @Override
    // 처음으로 세션에 소켓을 연결했을때 발생하는 이벤트
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        String sessionId = session.getId();
        String userName = getUserName(session);
        if(userName != "anonymous")
        {
            UserEntity entity = userRepository.findByName(userName).get(); // 어떤 유저라도 이름이 있으니까 그냥 get으로 받는다.
            Map<String, Object> attr = session.getAttributes();            // 속성 좀 추가해줄거임. 그러면 DB가 부담을 덜겠지.
            attr.put("no", entity.getUserNo());
            attr.put("name", entity.getName());
            attr.put("personalColor", entity.getPersonalColor());
        }

        storageBySessionId.put(sessionId, session);
        // 유저명 기반으로 저장할때는, 다중 접속을 감안하여 리스트에 저장한다.
        if(!storageByUserName.containsKey(userName))
        {
            List<WebSocketSession> list = new LinkedList<>();
            list.add(session);
            storageByUserName.put(userName, list);
        }
        else
        {
            storageByUserName.get(userName).add(session);
        }
        
        session.sendMessage(new TextMessage(sessionId));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage)
    {
        JSONObject object = new JSONObject();
        Map<String, Object> attr = session.getAttributes();

        object.put("for", "chat");
        object.put("sender", attr.get("name"));
        object.put("color", attr.get("personalColor"));
        object.put("content", textMessage.getPayload());

        String jsonString =  object.toString(); 


        for(WebSocketSession s : storageBySessionId.values())
        {
            try{
                s.sendMessage(new TextMessage(jsonString));
            }
            catch(IOException e)
            {
                
            }
            
        }

    }

    @Override
    // 클라이언트가 연결 해제될때 발생하는 이벤트
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        List<WebSocketSession> list = storageByUserName.get(getUserName(session));
        list.remove(session);
        if(list.isEmpty()) storageByUserName.remove(getUserName(session));

        storageBySessionId.remove(session.getId());
    }

    //scheduled
    @Scheduled(fixedDelay=1000)
    public void sendUsers()
    {
        String json = authenticUserList().toString();
        storageBySessionId.values().forEach(v -> {
            try {
                v.sendMessage(new TextMessage(json));

            } catch (IOException e) {
                
                e.printStackTrace();
            }
        });
    }


    ///////////////////
    // 몇가지 기능들
    //////////////////
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

    JSONObject authenticUserList()
    {
        JSONObject object = new JSONObject();

        JSONArray array = new JSONArray();
        for(String s : storageByUserName.keySet())
        {
            if(s.equals("anonymous")) continue;
            
            JSONObject info = new JSONObject();
            Map<String, Object> attr = storageByUserName.get(s).get(0).getAttributes();

            info.put("no", attr.get("no"));
            info.put("name", attr.get("name"));
            info.put("color", attr.get("personalColor"));

            array.put(info);
        }

        object.put("for", "userList");
        object.put("userCnt", storageBySessionId.size());
        object.put("users", array);

        return object;
    }


}
