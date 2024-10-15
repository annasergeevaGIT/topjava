package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, counter.incrementAndGet()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setUserId(counter.incrementAndGet());
            repository.put(meal.getUserId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        log.info("save {}", meal);
        return repository.computeIfPresent(meal.getUserId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        List<Meal> allMeals = new ArrayList<>();
        for (Meal meal : repository.values()) {
            if (meal.getUserId() == userId) meal.setUserId(0);
            allMeals.add(meal);
        }
        log.info("getAll");
        return allMeals;
    }
}