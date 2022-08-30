package code.config.security;

import code.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends Exception
{
    @Autowired
    private UserDetailsService LoginService;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        return http
                .authorizeRequests().antMatchers("/user/login").anonymous()
                .antMatchers("/**").permitAll()
                .and()
                .logout()                                                               // 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) // 로그아웃할 URL
                .logoutSuccessUrl("/")                                                  // 로그아웃 성공하면 갈 URL
                .invalidateHttpSession(true)                                            // 로그아웃 성공 시 세션 초기화
                .and()
                .csrf()                                                                 // 인터넷 보안 정책 (잠시 무력화시켰음)
                .ignoringAntMatchers("/**")
                .and()
                .userDetailsService(LoginService)                                       // 로그인 서비스에 이용되는 userDetailService를 상속받은 객체
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
