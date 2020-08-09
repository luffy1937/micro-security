package org.liuyuefeng.security.servergateway;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewayRateLimiterFilter extends OncePerRequestFilter{
    private RateLimiter rateLimiter = RateLimiter.create(1);
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("1 rate limit");
        if(rateLimiter.tryAcquire()){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }else {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("\"error\":\"too many request\"");
            httpServletResponse.getWriter().flush();
        }
    }
}
