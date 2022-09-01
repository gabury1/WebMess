package code.dto;

import lombok.*;

@Getter @Setter @ToString
@Builder @EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class MessageDto
{
    String type;
    String sender;
    String receiver;
    String channelId;
    Object data;

    public void newConnect()
    {
        type = "new";
    }
    public void closeConnect()
    {
        type = "close";
    }

}
