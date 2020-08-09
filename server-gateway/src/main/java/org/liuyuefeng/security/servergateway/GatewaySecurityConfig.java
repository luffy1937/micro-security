package org.liuyuefeng.security.servergateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableResourceServer
public class GatewaySecurityConfig extends ResourceServerConfigurerAdapter{
    @Autowired
    GatewayWebSecurityExpressionHandler gatewayWebSecurityExpressionHandler;
    @Autowired
    private GatewayAccessDeniedHandler gatewayAccessDeniedHandler;
    @Autowired
    GatewayAuthenticationEntryPoint gatewayAuthenticationEntryPoint;
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .authenticationEntryPoint(gatewayAuthenticationEntryPoint)//401
                .accessDeniedHandler(gatewayAccessDeniedHandler)//403
                .expressionHandler(gatewayWebSecurityExpressionHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http    .addFilterBefore(new GatewayRateLimiterFilter(), SecurityContextPersistenceFilter.class)
                .addFilterBefore(new GatewayAuditLogFilter(), ExceptionTranslationFilter.class)
                .authorizeRequests()
                .antMatchers("/token/**").permitAll()
                .anyRequest()
                .access("permissionService.hasPermission(request, authentication)");
    }
}
