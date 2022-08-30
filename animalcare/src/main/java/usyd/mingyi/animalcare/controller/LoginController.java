package usyd.mingyi.animalcare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.utils.FileStorage;
import usyd.mingyi.animalcare.utils.JasyptEncryptorUtils;
import usyd.mingyi.animalcare.utils.ResultData;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
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
    public  ResultData<Integer> signup(@RequestBody User userInfo){

        userInfo.setPassword(JasyptEncryptorUtils.encode(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        int i = userService.addUser(userInfo);
        if(i>=1){
            ResponseEntity.status(HttpStatus.OK);
            return ResultData.success(1);
        }else {
            ResponseEntity.status(401);
            return ResultData.fail(401,"signup fail");
        }

    }

    @PostMapping("/edit")
    public ResultData<Integer> updateUserInfo(@RequestBody User userInfo){

        int i = userService.updateUser(userInfo);
        if(i>=1){
            ResponseEntity.status(HttpStatus.OK);
            return ResultData.success(1);
        }else {
           // ResponseEntity.status(HttpStatus.OK);
            return ResultData.fail(400,"No such user");
        }


    }

    @PostMapping("/upload")
    @ResponseBody
    public ResultData<Integer> upLoadFile(@RequestParam("file") MultipartFile file) {

        String username = "/741917776";//假设当前用户为 741917776这个用户
        String path = ClassUtils.getDefaultClassLoader().getResource("public").getPath()+username;
        String access = null;
        try {
            access=FileStorage.SaveFile(file,path);
        } catch (IOException e) {
            e.printStackTrace();
            ResultData.fail(400,"fail");
            ResponseEntity.status(400); //也可以用 HttpStatus.BAD_REQUEST常量 方便后期维护 我就偷懒了 兄弟们待会写的时候别偷懒
        }
        System.out.println(access);//返回值为文件名
        String storage = username+"/"+access; //应该存在数据库的内容 其他电脑应该访问 ClassUtils.getDefaultClassLoader().getResource("public").getPath() +storage
        System.out.println(storage);
        System.out.println(path+"/"+access);
        return ResultData.success(1);
    }

    @GetMapping("/download")
    @ResponseBody
    public ResultData<byte[]> readFile(@RequestBody Map map) throws IOException {

        String path = (String) map.get("path");
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return ResultData.success(bytes);
    }


}
