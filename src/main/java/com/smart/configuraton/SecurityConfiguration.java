package com.smart.configuraton;

import com.smart.oauth2.CustomOAuth2UserService;
import com.smart.oauth2.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final String[] WHITE_LIST_URL = {
            "/oauth2/**",
            "/v3/api-docs",
            "/v2/api-docs",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/signIn",
            "/**"
    };

    private final String[] USER_URL = {
            "/user/**",
            "/chat/**"
    };

    private final String[] ADMIN_URL = {
            "/admin/**",
            "/chat/admin/**"
    };

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers(USER_URL)
                                .hasAuthority("ROLE_USER")
                                .requestMatchers(ADMIN_URL)
                                .hasAuthority("ROLE_ADMIN")
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(
                        form -> form
                                .loginPage("/signIn")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/redirect/")
                )
                .oauth2Login(
                        oauth -> oauth
                                .loginPage("/signIn")
                                .userInfoEndpoint(
                                        info -> info
                                                .userService(oAuth2UserService)
                                )
                                .successHandler(oAuth2LoginSuccessHandler)
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/signOut")
                )
                .authenticationProvider(authenticationProvider)
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(1)
                                .expiredUrl("/signIn")
                )
                .build();
    }
}
