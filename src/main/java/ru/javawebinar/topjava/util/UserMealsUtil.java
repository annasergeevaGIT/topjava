package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import javax.jws.soap.SOAPBinding;
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
        // TODO return filtered list with excess. Implement by cycles forEach
        //Filter meals according to the given time
        List<UserMeal> mealsFiltered = new ArrayList<>();
        for(UserMeal meal : meals){
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(),startTime,endTime)){
                mealsFiltered.add(meal);
            }
        }
        //System.out.println("Filtered Meals: " + mealsFiltered);

        //Count the calories for filtered meals
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap<>();
        for (UserMeal meal: mealsFiltered) {
            LocalDate date = meal.getDateTime().toLocalDate();
            caloriesSumPerDay.merge(date,meal.getCalories(), Integer:: sum);// (a, b) -> Integer.sum(a, b)
        }

        //Calculate excess
        List<UserMealWithExcess> excesses = new ArrayList<>();
        for(UserMeal meal : mealsFiltered){
            LocalDate date = meal.getDateTime().toLocalDate();
            boolean excess = caloriesSumPerDay.get(date) > caloriesPerDay;
            excesses.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
        }
        return excesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        return meals.stream()
                // Filter time
            .collect(Collectors.toMap(
                    meal -> meal.getDateTime().toLocalDate(), // key value
                    meal -> {
                        // create an object UserMealWithExcess
                        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), false);
                    },
                    (m1, m2) -> {
                        // count total
                        int totalCalories = m1.getCalories() + m2.getCalories();
                        // update excess
                        boolean excess = totalCalories > caloriesPerDay;
                        return new UserMealWithExcess(m1.getDateTime(), m1.getDescription(), totalCalories, excess);
                    }
            )).values().stream()
            // create a list
            .collect(Collectors.toList());
    }
}



/*
TASK:
Реализовать метод UserMealsUtil.filteredByCycles через циклы (forEach):

должны возвращаться только записи между startTime и endTime
поле UserMealWithExcess.excess должно показывать, превышает ли сумма калорий за весь день значение caloriesPerDay
Т. е. UserMealWithExcess - это запись одной еды, но поле excess будет одинаково для всех записей за этот день.

Проверьте результат выполнения ДЗ (можно проверить логику в http://javaops-demo.ru/topjava, список еды)
Оцените Time complexity алгоритма. Если она больше O(N), например O(NN) или Nlog(N), сделайте O(N).
Внимание: внимательно прочитайте про O(N). O - это любой коэффициент, 2*N это тоже O(N).

 Замечания к HW0
1: Код проекта менять можно! Одна из распространенных ошибок как в тестовых заданиях на собеседовании, так и при работе на проекте, что ничего нельзя менять. Конечно, при правках в рабочем проекте обязательно нужно проконсультироваться/проревьюироваться у авторов кода (находятся по истории VCS)
2: Наследовать UserMealWithExcess от UserMeal нельзя, т. к. это разные сущности: Transfer Object и Entity. Мы будем их проходить на 2-м уроке. Это относится и к их зависимости друг от друга.
3: Правильная реализация должна быть простой и красивой, можно сделать 2-мя способами: через стримы и через циклы. Сложность должна быть O(N), т. е. без вложенных стримов и циклов.
4: При реализации через циклы посмотрите в Map на методы getOrDefault или merge
5: При реализации через Stream заменяйте forEach оператором stream.map(..)
6: Объявляйте переменные непосредственно перед использованием (если возможно - сразу с инициализацией). При объявлении коллекций в качестве типа переменной используйте интерфейс (Map, List, ..)
7: Если IDEA предлагает оптимизацию (желтым подчеркивает), например, заменить лямбду на ссылку на метод (method reference), соглашайтесь (Alt+Enter)
8: Пользуйтесь форматированием кода в IDEA: Alt+Ctrl+L
9: Перед check-in (отправкой изменений на GitHub) просматривайте внесенные изменения (Git -> Log -> курсор на файл и Ctrl+D): не оставляйте в коде ничего лишнего (закомментированный код, TODO и пр.). Если файл не меняется (например только пробелы или переводы строк), не надо его чекинить, делайте ему revert (Git -> Revert / Ctrl+Alt+Z).
10: System.out.println нельзя использовать нигде, кроме как в main. Позже введем логирование.
11: Результаты, возвращаемые UserMealsUtil.filteredByStreams, мы будем использовать в нашем приложении для фильтрации по времени и отображения еды правильным цветом.
12: Обращайте внимание на комментарии к вашим коммитам в Git. Они должны быть короткие и информативные (лучше на english)
13: Не полагайтесь в решении на то, что список еды будет подаваться отсортированным. Такого условия нет.


 */
