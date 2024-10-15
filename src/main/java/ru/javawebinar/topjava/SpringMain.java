package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepository;
import ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import java.util.Arrays;

public class SpringMain {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));

            InMemoryUserRepository inMemoryUserRepository = appCtx.getBean(InMemoryUserRepository.class);
            InMemoryMealRepository inMemoryMealRepository = appCtx.getBean(InMemoryMealRepository.class);

            // Test data for users
            User user1 = new User(null, "User One", "userone@mail.com", "pw", Role.USER);
            User user2 = new User(null, "User Two", "usertwo@mail.com", "pw", Role.USER);
            inMemoryUserRepository.save(user1);
            inMemoryUserRepository.save(user2);

            // Retrieve user by email
            User retrievedUser = inMemoryUserRepository.getByEmail("userone@mail.com");
            System.out.println("Retrieved User: " + retrievedUser);

            // Display all users
            System.out.println("All users: " + inMemoryUserRepository.getAll());

            // Display all meals for userId=1
            System.out.println("Meals for userId=1: " + inMemoryMealRepository.getAll(1));

            // Display all meals for userId=2
            System.out.println("Meals for userId=2: " + inMemoryMealRepository.getAll(2));
        }
    }
}
