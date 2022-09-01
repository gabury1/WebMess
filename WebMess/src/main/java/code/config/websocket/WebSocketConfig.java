package code.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer
{
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingSocketHandler(), "/room")
                .setAllowedOrigins("http://localhost:8080") // 루트 URL 제대로 안해주면 접속할 수 없다. '*'로 처리하지 마라.
                .withSockJS();
    }

    @Bean
    public WebSocketHandler signalingSocketHandler()
    {
        return new WebSocketHandler();
    }
}
