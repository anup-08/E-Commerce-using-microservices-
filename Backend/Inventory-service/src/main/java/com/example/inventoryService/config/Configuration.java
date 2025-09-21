package com.example.inventoryService.config;

import org.springframework.context.annotation.Bean;
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

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request ->
                        request.requestMatchers("inventory/add/stock").hasRole("CUSTOMER")
                                .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(abstractAuthenticationTokenConverter())))

                .sessionManagement(session ->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> abstractAuthenticationTokenConverter(){
        return jwt -> {
            Map<String , Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

            if(realmAccess == null || realmAccess.get("roles") == null){
                return new JwtAuthenticationToken(jwt, List.of());
            }

            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            List<GrantedAuthority> gAuth = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                    .collect(Collectors.toList());

            return new  JwtAuthenticationToken(jwt , gAuth , jwt.getSubject());
        };
    }
}
