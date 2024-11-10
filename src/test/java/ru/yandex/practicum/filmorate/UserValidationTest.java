package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserValidationTest {

    @Test
    public void testUserValidation_validUser() {
        User user = new User();
        user.setEmail("validemail@example.com");
        user.setLogin("validLogin");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertDoesNotThrow(() -> validateUser(user));
    }

    @Test
    public void testUserValidation_invalidEmail() {
        User user = new User();
        user.setEmail("invalidemail");

        ValidationException exception = assertThrows(ValidationException.class, () -> validateUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'.", exception.getMessage());
    }

    @Test
    public void testUserValidation_invalidLogin() {
        User user = new User();
        user.setLogin("invalid login");
        user.setEmail("valid@mail");
        ValidationException exception = assertThrows(ValidationException.class, () -> validateUser(user));
        assertEquals("Логин не может быть пустым и не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void testUserValidation_invalidBirthday() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));
        user.setEmail("valid@mail");
        user.setLogin("safaf");
        ValidationException exception = assertThrows(ValidationException.class, () -> validateUser(user));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
