package org.liuyuefeng.security.serverauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }


/*
客户端获取token
其中 basic是 username:orderApp password:123456
form中username/password代表用户
curl --location --request POST 'http://localhost:9090/oauth/token' \
            --header 'Authorization: Basic b3JkZXJBcHA6MTIzNDU2' \
            --form 'username=liuyuefeng' \
            --form 'password=123456' \
            --form 'grant_type=password' \
            --form 'scope=write read'


sourceServer验证token接口是 /oauth/check_token
 */

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("orderApp")
                .secret(passwordEncoder.encode("123456"))
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds("order-service")
                .authorizedGrantTypes("password")
                .and()
                .withClient("orderService")
                .secret(passwordEncoder.encode("123456"))
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .resourceIds("order-service")
                .authorizedGrantTypes("password");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("isAuthenticated()");
    }
}
