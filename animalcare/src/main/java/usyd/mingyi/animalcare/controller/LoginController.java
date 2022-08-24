package usyd.mingyi.animalcare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.utils.ResultData;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;

@RestController
public class LoginController {

    @Autowired
    UserService userService;

/*    public User queryUser(@RequestParam("username") String username,@RequestParam("password")String password){
        User user = userService.queryUser(username, password);
        System.out.println(user);
        return user;
    }*/

    @PostMapping("/login")
    public ResultData<User> login(@RequestParam("username") String username,
                        @RequestParam("password") String password){
        User user = userService.queryUser(username, password);
        System.out.println(user);

        if(user!=null){
            return ResultData.success(user);
        }else {
            return ResultData.fail(999,"Error");
        }


    }


}
