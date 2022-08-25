package usyd.mingyi.animalcare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.utils.JasyptEncryptorUtils;
import usyd.mingyi.animalcare.utils.ResultData;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;


import java.util.UUID;

@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    UserService userService;


    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    public ResultData<User> login(@RequestBody User userInfo){

        String username = userInfo.getUserName();
        String password = userInfo.getPassword();
        String encryptedPassword = userService.queryPassword(username);

        if (encryptedPassword==null){
            return ResultData.fail(401,"No such user");
        }else {
            String decode = JasyptEncryptorUtils.decode(encryptedPassword);
            if(!decode.equals(password)){
               return ResultData.fail(401,"Password error");
            }
        }

        User user = userService.queryUser(username, encryptedPassword);


        if(user!=null){

            ResponseEntity.status(HttpStatus.OK);// Status Code 200
            return ResultData.success(user);
        }else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED);// Status Code 401
            return ResultData.fail(401,"Error");
        }


    }

    @PostMapping("/signup")
    public  void signup(@RequestBody User userInfo){

        userInfo.setPassword(JasyptEncryptorUtils.encode(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        int i = userService.addUser(userInfo);
        ResponseEntity.status(HttpStatus.OK);


    }


}
