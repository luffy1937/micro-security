package org.liuyuefeng.security.servergateway.filter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewayAuditLogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("1. add log for " + user);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        System.out.println("3. update log to success    ");
    }
}
