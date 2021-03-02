package ru.job4j.dreamjob.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.dreamjob.store.CityStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Gson gson = new GsonBuilder().create();
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        writer.println(gson.toJson(CityStore.instOf().allCities()));
        writer.flush();
    }
}
