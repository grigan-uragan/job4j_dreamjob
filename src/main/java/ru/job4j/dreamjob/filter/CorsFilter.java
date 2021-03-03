package ru.job4j.dreamjob.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods",
                "GET, OPTIONS, PUT, DELETE, POST");
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
         if (req.getMethod().equals("OPTIONS")) {
             resp.setStatus(HttpServletResponse.SC_ACCEPTED);
             return;
         }
         filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

    }
}
