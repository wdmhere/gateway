package com.ha.net.eautoopen.config;

import com.ha.net.eautoopen.auth.handler.JwtAuthStandardHandler;
import com.ha.net.eautoopen.auth.jwt.base.impl.JwtGeneralAuth;
import com.ha.net.eautoopen.filter.factory.TokenGatewayFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    JwtGeneralAuth jwtGeneralAuth;

    @Bean
    public JwtAuthStandardHandler jwtAuthStandardHandler(){
        return new JwtAuthStandardHandler(jwtGeneralAuth);
    }

    @Bean
    public TokenGatewayFilterFactory tokenGatewayFilterFactory(){
        return new TokenGatewayFilterFactory();
    }
}
