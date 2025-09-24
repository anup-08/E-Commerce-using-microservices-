package com.cartService.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(request -> request.anyRequest().hasRole("USER"))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAbstractAuthenticationTokenConverter())
                ))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public Converter<Jwt , AbstractAuthenticationToken> jwtAbstractAuthenticationTokenConverter (){
        return new Converter<Jwt, AbstractAuthenticationToken>() {
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {
                Map<String,Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

                if(realmAccess == null || realmAccess.get("roles") == null){
                    return new JwtAuthenticationToken(jwt, List.of());
                }
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                List<GrantedAuthority> gAuth = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                        .collect(Collectors.toList());

                return new JwtAuthenticationToken(jwt, gAuth, jwt.getSubject());
            }
        };
    }
}
