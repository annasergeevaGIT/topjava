package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

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

    // MealService instance for handling business logic
    private final MealService service = new MealService(); // Consider dependency injection in a Spring context

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = SecurityUtil.authUserId();
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all meals for the authorized user
                List<MealTo> meals = service.getAll(userId);
                request.setAttribute("meals", meals);
                request.getRequestDispatcher("/WEB-INF/jsp/meals.jsp").forward(request, response);
            } else {
                // Extract the meal ID from the URL and fetch the specific meal
                int id = extractIdFromPath(pathInfo);
                Meal meal = service.get(id, userId);
                MealTo mealTo = MealsUtil.createTo(meal, meal.getCalories() > MealsUtil.DEFAULT_CALORIES_PER_DAY);
                request.setAttribute("meal", mealTo);
                request.getRequestDispatcher("/WEB-INF/jsp/mealDetails.jsp").forward(request, response);
            }
        } catch (NotFoundException e) {
            log.warn("Meal not found or unauthorized access for userId={}, mealId={}", userId, pathInfo);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Meal not found");
        } catch (Exception e) {
            log.error("Error retrieving meals for userId={}", userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = SecurityUtil.authUserId();

        try {
            int id = extractIdFromRequest(request);
            Meal meal = new Meal(
                    id,
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories"))
            );

            log.info(meal.isNew() ? "Creating new meal for userId={}" : "Updating meal id={} for userId={}", meal.getUserId(), userId);
            MealTo savedMeal = service.create(meal, userId);
            response.sendRedirect(request.getContextPath() + "/api/meals/" + savedMeal.getId());
        } catch (Exception e) {
            log.error("Error saving meal for userId={}", userId, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid meal data");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = SecurityUtil.authUserId();
        int id = extractIdFromPath(request.getPathInfo()); // Declare id before try block

        try {
            service.delete(id, userId);
            log.info("Deleted meal id={} for userId={}", id, userId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NotFoundException e) {
            log.warn("Meal not found or unauthorized delete for userId={}, mealId={}", id, userId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Meal not found");
        } catch (Exception e) {
            log.error("Error deleting meal id={} for userId={}", id, userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to extract the ID from the URL path
    private int extractIdFromPath(String pathInfo) {
        try {
            String[] pathParts = pathInfo.split("/");
            return Integer.parseInt(pathParts[pathParts.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid meal ID format", e);
        }
    }

    // Helper method to extract the ID from the request parameters
    private int extractIdFromRequest(HttpServletRequest request) {
        String id = request.getParameter("id");
        return (id != null && !id.isEmpty()) ? Integer.parseInt(id) : 0;
    }
}
