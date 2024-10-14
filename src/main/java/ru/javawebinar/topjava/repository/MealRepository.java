package ru.javawebinar.topjava.repository;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
@Repository
public interface MealRepository {
    // null if updated meal does not belong to userId
    Meal save(Meal meal, int usrId);

    // false if meal does not belong to userId
    boolean delete(int id, int usrId);

    // null if meal does not belong to userId
    Meal get(int id, int usrId);

    // ORDERED dateTime desc
    Collection<Meal> getAll(int usrId);
}
