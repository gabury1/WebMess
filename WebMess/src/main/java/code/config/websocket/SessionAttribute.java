package code.config.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Getter
public enum SessionAttribute
{
    AUTHENTICATED("AUTHENTICATED"),
    USER_INFO("USER_INFO");

    private final String key;

}
