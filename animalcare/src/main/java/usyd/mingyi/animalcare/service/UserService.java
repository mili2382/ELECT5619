package usyd.mingyi.animalcare.service;

import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.pojo.User;


public interface UserService {
    User queryUser(String username, String password);

    void addUser(User user);

    String queryPassword(String username);

    int updateUser(User user);
}
