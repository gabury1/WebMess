package code.dto;

import java.util.LinkedList;
import java.util.List;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode

public class StompClient 
{
    Long no;
    String name;
    String email;
    
    String colorCode;
    String colorName;
    String introduce;

    // 현재 소유하고 있는 세션
    List<String> sessionIds;

    // 내가 속한 방들

}
