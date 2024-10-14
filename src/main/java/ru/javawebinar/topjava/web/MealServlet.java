package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int CALORIES_PER_DAY = 2000; // calories norm

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("display meals");

        // Convert to List<MealTo> with the excess flag calculated
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(MealsUtil.meals, LocalTime.of(7, 0), LocalTime.of(12, 0), CALORIES_PER_DAY);
        request.setAttribute("meals", mealsTo);

        // Forward to JSP
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
