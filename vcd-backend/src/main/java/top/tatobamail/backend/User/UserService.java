package top.tatobamail.backend.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    boolean checkUsernameExists(String username);

    User login(User user);
}
