package usyd.mingyi.animalcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.User;

@Service
public class UserServiceImp implements  UserService{

    @Autowired
    UserMapper userMapper;

    @Override
    public User queryUser(String username, String password) {
        return  userMapper.queryUser(username,password);
    }

    @Override
    public void addUser(User user) {
        userMapper.addUser(user);
    }

    @Override
    public String queryPassword(String username) {
        return userMapper.queryPassword(username);
    }

    @Override
    public void updateUser(User user) {

    }
}
