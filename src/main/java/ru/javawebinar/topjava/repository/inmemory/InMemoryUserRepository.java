package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User save(User user) {
        if (user.isNew()) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(int id) {
        return users.get(id);
    }

    @Override
    public User getByEmail(String email) {
        return users.get(email);
    }

    @Override
    public boolean delete(int id) {
        return users.remove(id) != null;
    }

    @Override
    public List<User> getAll() {
        return users.values()
                .stream()
                .sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList());
    }
}