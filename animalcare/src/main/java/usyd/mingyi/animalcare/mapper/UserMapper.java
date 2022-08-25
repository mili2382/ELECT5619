package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import usyd.mingyi.animalcare.pojo.User;

@Mapper
@Repository
public interface UserMapper {
    User queryUser(String username,String password);
    void addUser(User user);
    String queryPassword(String username);
}
