package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    @Autowired
    private MealRepository repository;

    public Meal get(int id, int userId) {
        Meal meal = repository.get(id, userId);
        if (meal == null || meal.getUserId() != userId) {
            throw new NotFoundException("Meal not found or does not belong to the user");
        }
        return meal;
    }

    public MealTo create(Meal meal, int userId) {
        meal.setUserId(userId);
        return MealsUtil.createTo(repository.save(meal, userId), meal.getCalories() > MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(meal, userId), meal.getUserId());
    }

    public void delete(int id, int userId) {
        if (!repository.delete(id, userId)) {
            throw new NotFoundException("Meal not found or does not belong to the user");
        }
    }

    public List<MealTo> getAll(int userId) {
        return MealsUtil.getTos(repository.getAll(userId), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }
}

