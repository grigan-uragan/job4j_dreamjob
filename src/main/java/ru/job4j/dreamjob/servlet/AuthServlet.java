package ru.job4j.dreamjob.servlet;

import ru.job4j.dreamjob.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if ("root@local.ru".equals(email) && "root".equals(password)) {
            HttpSession session = req.getSession();
            User user = new User();
            user.setName("Admin");
            user.setEmail(email);
            user.setPassword(password);
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/posts.do");
        } else {
            req.setAttribute("error", "неверный email или пароль ");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
