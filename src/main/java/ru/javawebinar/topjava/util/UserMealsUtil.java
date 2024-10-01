package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);


        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        //Count the calories per day
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();
        for (UserMeal meal: meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            caloriesSumPerDay.merge(date,meal.getCalories(), Integer:: sum);// (a, b) -> Integer.sum(a, b)
        }

        //Calculate excess
        List<UserMealWithExcess> result = new ArrayList<>();
        for(UserMeal meal : meals){
            if(TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(),startTime,endTime)) {
                LocalDate date = meal.getDateTime().toLocalDate();
                boolean excess = caloriesSumPerDay.get(date) > caloriesPerDay;
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Count sum of calories per day
        Map<LocalDate, Integer> caloriesSumPerDay = meals.stream()
                .collect(Collectors.toMap(
                        meal -> meal.getDateTime().toLocalDate(),
                        UserMeal::getCalories, // (a, b) -> UserMeal.getCalories(a, b)
                        Integer::sum // (a, b) -> Integer.sum(a, b)
                ));
        //Filter by time and create UserMealWithExcess
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> {
                    LocalDate date = meal.getDateTime().toLocalDate();
                    boolean excess = caloriesSumPerDay.get(date) > caloriesPerDay;
                    return new UserMealWithExcess(meal.getDateTime(),meal.getDescription(),meal.getCalories(), excess);
                })
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate())) // Group by day
                .values().stream() // Stream over the grouped meal lists
                .flatMap(dailyMeals -> {
                    // Count daily calories
                    int totalCaloriesPerDay = dailyMeals.stream().mapToInt(UserMeal::getCalories).sum();
                    boolean excess = totalCaloriesPerDay > caloriesPerDay;

                    // Filter by time, convert to UserMealWithExcess, and add excess flag
                    return dailyMeals.stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
                })
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreams3(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        //Group by day
        Map<LocalDate, List<UserMeal>> mealsByDay = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()));

        //Make set from values
        return mealsByDay.entrySet().stream()
                .flatMap(entry -> {
                    List<UserMeal> dailyMeals = entry.getValue();

                    // Count daily calories
                    int totalCaloriesPerDay = dailyMeals.stream().mapToInt(UserMeal::getCalories).sum();
                    boolean excess = totalCaloriesPerDay > caloriesPerDay;

                    // Filter by time, convert to UserMealWithExcess, add excess
                    return dailyMeals.stream()
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
                })
                .collect(Collectors.toList());
    }
}