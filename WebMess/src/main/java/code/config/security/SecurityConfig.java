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

@EnableWebSecurity
@Configuration
public class SecurityConfig extends Exception
{
    @Autowired
    private UserDetailsService LoginService;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        return http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/room/**").permitAll()                    // 웹소켓 url 허용
                .antMatchers("/ws/**").permitAll()                      // STOMP 허용
                .antMatchers( "/css/**", "/js/**", "/img/**", "/lib/**").permitAll() // js 등등을 못 불러오면 안되니까...
                .antMatchers("/user/signup").anonymous()
                .antMatchers("/user/**").permitAll()                    // 유저 관련 요청은 오케이
                .antMatchers("/**").authenticated()
                .and()
                .formLogin()                                                            // 로그인 설정(추후 커스텀 예정)
                .defaultSuccessUrl("/")                                                 // 로그인 성공 시 이동할 URL
                .and()
                .logout()                                                               // 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) // 로그아웃할 URL
                .logoutSuccessUrl("/")                                                  // 로그아웃 성공하면 갈 URL
                .invalidateHttpSession(true)                                            // 로그아웃 성공 시 세션 초기화
                .and()
                .cors().disable()
                .csrf().disable()                                                       // 인터넷 보안 정책 (잠시 무력화시켰음)
                .userDetailsService(LoginService)                                       // 로그인 서비스에 이용되는 userDetailService를 상속받은 객체
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
