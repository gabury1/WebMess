package code.config.websocket;

import jdk.internal.org.jline.utils.Log;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@Getter @Setter @Builder
public class WebSocketHandler extends TextWebSocketHandler
{
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    // 처음으로 세션에 소켓을 연결했을때 발생하는 이벤트
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        Log.info(sessionId);

    }

    @Override
    //클라이언트들에게 메시지를 발송
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage)
    {

    }

    @Override
    // 클라이언트가 연결 해제될때 발생하는 이벤트
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        sessions.remove(session);
    }


}
