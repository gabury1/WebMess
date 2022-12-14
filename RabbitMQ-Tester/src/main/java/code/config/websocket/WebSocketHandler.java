package code.config.websocket;

import code.dto.MessageDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
@Getter
@Setter
@Log4j2
@Builder
public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        MessageDto message = MessageDto.builder()
                .sender(sessionId)
                .receiver("all")
                .build();
        message.newConnect();

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        MessageDto message = new MessageDto();
        message.setData(textMessage.getPayload());
        message.setSender(session.getId());
        log.info(message.toString());

        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(message.getData().toString()));
            } catch (Exception e) {
                log.info("메시지 오류!");
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();

        sessions.remove(sessionId);

        MessageDto message = new MessageDto();
        message.closeConnect();
        message.setSender(sessionId);

    }

}
