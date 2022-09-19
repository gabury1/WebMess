package code.config.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.mysql.cj.Session;


@Configuration
@EnableWebSocketMessageBroker
public class StompConfigurer implements WebSocketMessageBrokerConfigurer
{
    @Autowired
    SessionStorage sessionStorage;

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
                ;
    }

    @Bean
    public SessionStorage storage()
    {
        
        return new SessionStorage();
    }

}
