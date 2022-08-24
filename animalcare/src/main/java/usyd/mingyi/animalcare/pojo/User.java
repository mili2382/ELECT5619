package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

//用户信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;
    private String userName;
    private String password;
    private String email;
    private String uuid;
    private String userImageAddress;
    private List<Post> postList;
    private List<Pet> petList;
    private List<User> friendsList;

}
