package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.service.MealService;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealRestController {
    @Autowired
    private MealService service;

    @GetMapping("/{id}")
    public MealTo get(@PathVariable int id) {
        return service.get(id);
    }

    @PostMapping
    public MealTo create(@RequestBody Meal meal) {
        return service.create(meal);
    }

    @PutMapping("/{id}")
    public MealTo update(@PathVariable int id, @RequestBody Meal meal) {
        return service.update(id, meal);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @GetMapping
    public List<MealTo> getAll() {
        return service.getAll();
    }
}
