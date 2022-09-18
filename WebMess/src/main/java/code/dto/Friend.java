package code.dto;

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
public class Friend 
{
    Long no;
    String name;
    String email;
    
    String colorCode;
    String colorName;
    String introduce;

}
