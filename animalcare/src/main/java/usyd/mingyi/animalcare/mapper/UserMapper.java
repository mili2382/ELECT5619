package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    User queryUser(String username,String password);
    int addUser(User user);
    String queryPassword(String username);
    int updateUser(User user);
    User queryUserByUsername(String username);
    User queryUserById(int userId);
//    List<User> queryFriendListById(int userId);
}
