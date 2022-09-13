package code.dto;

import java.util.LinkedList;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import code.domain.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode

public class WebSocketClient 
{

    Long no;
    String name;
    String email;
    
    String colorCode;
    String colorName;
    String introduce;

    // 현재 소유하고 있는 세션
    List<WebSocketSession> sessions;
    
    // 현재 친구들
    List<UserEntity> friends;
    // 내가 속한 방들

}
