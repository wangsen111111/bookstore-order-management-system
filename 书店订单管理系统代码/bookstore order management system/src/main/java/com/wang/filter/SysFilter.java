package com.wang.filter;

import com.wang.pojo.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/jsp/*")
public class SysFilter implements Filter {


    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) request;
        HttpServletResponse rp = (HttpServletResponse) response;
        //过滤器，从session中获取用户
        User userSession = (User) rq.getSession().getAttribute("userSession");
        if (null == userSession) {
            //用户没有登陆等
            rp.sendRedirect("/bookstore_order_management_system_war/error.jsp");
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {

    }
}
