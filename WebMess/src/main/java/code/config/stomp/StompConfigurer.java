package code.config.stomp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class StompConfigurer implements WebSocketMessageBrokerConfigurer
{

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        // Websocket STOMP 설정. 이 정도면 작동은 된다.
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS()
                .setInterceptors(handShakeHandler());
                ;
    }

    @Bean
    public HandShaker handShakeHandler()
    {
        
        return new HandShaker();
    }

}
