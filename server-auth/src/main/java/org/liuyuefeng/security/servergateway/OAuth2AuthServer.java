package org.liuyuefeng.security.servergateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailService;
    @Bean
    public TokenStore tokenStore(){
        //return new JdbcTokenStore(dataSource);
        return new JwtTokenStore(jwtTokenEnhancer());
    }
    @Bean
    private JwtAccessTokenConverter jwtTokenEnhancer() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //jwtAccessTokenConverter.setSigningKey("123456");
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("liu.key"), "123456".toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("liu"));
        return jwtAccessTokenConverter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .userDetailsService(userDetailService)
                .tokenStore(tokenStore())
                .tokenEnhancer(jwtTokenEnhancer())
                .authenticationManager(authenticationManager);
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
public static void main(String[] args) {
    System.out.println(new BCryptPasswordEncoder().encode("123456"));
}

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
 /*       clients.inMemory()
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
*/
 clients.jdbc(dataSource);
}

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()");
    }
}
