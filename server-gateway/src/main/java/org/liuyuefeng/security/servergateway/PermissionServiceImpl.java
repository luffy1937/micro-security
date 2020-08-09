package org.liuyuefeng.security.servergateway;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class PermissionServiceImpl implements PermissionService{

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        System.out.println(request.getRequestURI());
        System.out.println(ReflectionToStringBuilder.toString(authentication));
        return RandomUtils.nextBoolean();

    }
}
