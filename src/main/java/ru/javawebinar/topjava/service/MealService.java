package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

@Service
public class MealService {
    @Autowired
    private MealRepository repository;

    public MealTo get(int id) {
        Meal meal = repository.get(id, SecurityUtil.authUserId());
        if (meal == null) {
            throw new NotFoundException("Meal not found");
        }
        return MealsUtil.createTo(meal, meal.getCalories() > MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public MealTo create(Meal meal) {
        meal.setUserId(SecurityUtil.authUserId());
        return MealsUtil.createTo(repository.save(meal, meal.getUserId()), meal.getCalories() > MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public MealTo update(int id, Meal meal) {
        meal.setUserId(SecurityUtil.authUserId());
        return MealsUtil.createTo(repository.save(meal, meal.getUserId()), meal.getCalories() > MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public void delete(int id) {
        repository.delete(id, SecurityUtil.authUserId());
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(repository.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }
}
