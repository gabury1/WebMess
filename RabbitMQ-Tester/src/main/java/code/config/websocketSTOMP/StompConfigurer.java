package code.config.websocketSTOMP;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import reactor.netty.tcp.TcpClientConfig;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfigurer implements WebSocketMessageBrokerConfigurer
{
    // 메시지 브로커 설정(STOMP)
    // 메시지를 받는 주체는 특정 url을 구독함(subscribe)으로써 메시지를 받아볼 수 있다.
    // 메시지 발행자는 특정 url(@MessageMapping)에 메시지를 전달하는 것으로, 메시지를 발행하고, 서버는 이를 받아서 구독 url에 전달해준다.

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        // Websocket STOMP 설정. 이 정도면 작동은 된다.
        registry.setApplicationDestinationPrefixes("/pub")
                .enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(5672)
                .setClientLogin("LYST")
                .setClientPasscode("LYST")
                ;

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS()
                ;

    }


}
