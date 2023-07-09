package com.ruiji.filter;

import com.alibaba.fastjson.JSON;
import com.ruiji.common.BaseContext;
import com.ruiji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER =  new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String uri = httpServletRequest.getRequestURI();
        String urls[] = new String[]{ //不需要处理
                "/employee/logout",
                "/employee/login",
                "/user/**",
                "/common/**",
                "/backend/**",
                "/front/**",
                "/test/**",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        log.info("开始匹配{}",uri);
        if(check(urls, uri)) {
            log.info("静态或登录资源，通过");
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        if(httpServletRequest.getSession().getAttribute("employee") != null) {
            log.info("已登录，通过");
            Long id = (Long)httpServletRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
//            long id = Thread.currentThread().getId();
//            log.info("当前线程{}",id);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        if(httpServletRequest.getSession().getAttribute("user") != null) {
            log.info("已登录，通过");
            Long userId = (Long)httpServletRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        log.info("未登录，拦截");
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls, String uri) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if (match) return true;
        }
        return false;
    }

}
