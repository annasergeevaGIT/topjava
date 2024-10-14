package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private int nextId = 1;

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setUserId(nextId++);
        }
        meal.setUserId(userId);
        repository.put(meal.getUserId(), meal);
        return meal;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return (meal != null && meal.getUserId().equals(userId)) ? meal : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = repository.get(id);
        if (meal != null && meal.getUserId().equals(userId)) {
            repository.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.values()
                .stream()
                .filter(meal -> meal.getUserId().equals(userId))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

