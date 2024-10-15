package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/meals/*")
public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private final MealService service = new MealService(); // Initialize your service class here

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<MealTo> meals = service.getAll();
            request.setAttribute("meals", meals);
            request.getRequestDispatcher("/WEB-INF/jsp/meals.jsp").forward(request, response);
        } else {
            int id = extractIdFromPath(pathInfo);
            MealTo meal = service.get(id);
            if (meal != null) {
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/WEB-INF/jsp/mealDetails.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = extractIdFromRequest(request);
        Meal meal = new Meal(
                id,
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        MealTo savedMeal = service.create(meal);
        response.sendRedirect(request.getContextPath() + "/api/meals/" + savedMeal.getId());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = extractIdFromPath(request.getPathInfo());
        service.delete(id);
        log.info("Deleted meal with id={}", id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private int extractIdFromPath(String pathInfo) {
        String[] pathParts = pathInfo.split("/");
        return Integer.parseInt(pathParts[pathParts.length - 1]);
    }

    private int extractIdFromRequest(HttpServletRequest request) {
        String id = request.getParameter("id");
        return (id != null && !id.isEmpty()) ? Integer.parseInt(id) : 0;
    }
}
