package com.sbs.tutorial.app1.base.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.ignoringRequestMatchers("/**")) // csrf 허용
        .authorizeHttpRequests(auth -> auth
            // Chrome DevTools 경로 허용
            .requestMatchers("/.well-known/**").permitAll()
            .requestMatchers("/", "/member/join", "/member/login", "/error").permitAll() // permitAll : 접속 허용
            // 정적 리소스
            .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/gen/**", "/member/profile/img/**").permitAll()
            .anyRequest().authenticated() // 위 사항을 제외하고는 로그인 필요
        ).formLogin(form -> form
            .loginPage("/member/login") // GET : 로그인 페이지
            .loginProcessingUrl("/member/login") // POST : 로그인 처리
            .defaultSuccessUrl("/")
            .permitAll()
        ).oauth2Login(Customizer.withDefaults() // 최소한의 설정
        ).logout(logout -> logout
            .logoutUrl("/member/logout") // POST : 로그아웃
            .logoutSuccessUrl("/") // 로그아웃 성공시 리다이렉트
            .permitAll()
        );

    return http.build();
  }

//  @Bean
//  public WebSecurityCustomizer webSecurityCustomizer() {
//    return (web) -> web.ignoring()
//        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
//        .requestMatchers("/favicon.ico", "/resources/**", "/error");
//  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
    // 스프링 시큐리티 인증을 처리
    // 커스텀 인증 로직을 구현할 때 필요
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
