package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public User save(User user) {
        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            users.put(user.getId(), user);
        } else if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            return null; // ID not found, no update performed
        }
        // handle case: update, but not present in storage
        log.info("save {}", user);
        return users.computeIfPresent(user.getId(), (id, oldMeal) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return users.get(id);
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return users.remove(id) != null;
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        return users.values().stream()
                .sorted(Comparator.comparing(User::getName)//order by name then by email
                        .thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }
}