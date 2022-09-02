package usyd.mingyi.animalcare.controller;


import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.utils.FileStorage;
import usyd.mingyi.animalcare.utils.JasyptEncryptorUtils;
import usyd.mingyi.animalcare.utils.ResultData;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.Verification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    UserService userService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JavaMailSender mailSender;


    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User userInfo){

        String username = userInfo.getUserName();
        String password = userInfo.getPassword();
        String encryptedPassword = userService.queryPassword(username);

        if (encryptedPassword==null){
            return new ResponseEntity<>(ResultData.fail(401,"No such user"),HttpStatus.UNAUTHORIZED);
        }else {
            String decode = JasyptEncryptorUtils.decode(encryptedPassword);
            if(!decode.equals(password)){
                return new ResponseEntity<>(ResultData.fail(401,"Password error"),HttpStatus.UNAUTHORIZED);

            }
        }

        User user = userService.queryUser(username, encryptedPassword);


        if(user!=null){

            return new ResponseEntity<>(ResultData.success(user),HttpStatus.OK);

        }else {
            return new ResponseEntity<>(ResultData.fail(401,"Password error"),HttpStatus.UNAUTHORIZED);

        }


    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody User userInfo){

        userInfo.setPassword(JasyptEncryptorUtils.encode(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        int i = userService.addUser(userInfo);
        if(i>=1){
            return new ResponseEntity<>(ResultData.success("Signup success"),HttpStatus.OK);

        }else {
            return new ResponseEntity<>(ResultData.fail(401,"Signup fail"),HttpStatus.BAD_REQUEST);

        }

    }

    @GetMapping("/username")
    public ResponseEntity<Object> usernameCheck(@RequestBody Map map){
        String userName =(String) map.get("userName");
        User user = userService.queryUserByUsername(userName);
        if(user == null){
            return new ResponseEntity<>(ResultData.success(true), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultData.fail(400,"Fail"), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/email")
    public ResponseEntity<Object> sendEmailByUsername(@RequestBody Map map){
        String email =(String) map.get("email");
        String userName = (String) map.get("userName");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("Verification Code");
        int i = new Random().nextInt(1000000);
        String code = String.format("%06d", i);
        mailMessage.setText("This is your one time verification code :" + code);
        mailMessage.setTo(email);
        mailMessage.setFrom("LMY741917776@gmail.com");
        mailSender.send(mailMessage);
        Verification.putCode(userName,code);
        return new ResponseEntity<>(ResultData.success("Send success"), HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validateCode(@RequestBody Map map){
        String code = (String) map.get("code");
        String userName = (String) map.get("userName");
        if(Verification.hasUser(userName)){
            if(Verification.getCode(userName).equals(code)){
            return new ResponseEntity<>(ResultData.success("Code equal"), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(ResultData.fail(400,"Code not equal"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(ResultData.fail(400,"No code in the system"), HttpStatus.BAD_REQUEST);
    }




    @PostMapping("/edit")
    public ResponseEntity<Object> updateUserInfo(@RequestBody User userInfo){

        int i = userService.updateUser(userInfo);
        if(i>=1){
            return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);

        }else {
            return new ResponseEntity<>(ResultData.fail(400,"Update fail"), HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping("/upload")
    public ResultData<Integer> upLoadFile(@RequestParam("file") MultipartFile file) {

        String username = "/741917776";//假设当前用户为 741917776这个用户
        String path = ClassUtils.getDefaultClassLoader().getResource("public").getPath()+username;
        String access = null;
        try {
            access=FileStorage.SaveFile(file,path);
        } catch (IOException e) {
            e.printStackTrace();
            ResponseEntity.status(400); //也可以用 HttpStatus.BAD_REQUEST常量 方便后期维护 我就偷懒了 兄弟们待会写的时候别偷懒
            return ResultData.fail(400,"Fail to upload");

        }

       //将要存的文件名存到对应文件夹中

        return ResultData.success(1);
    }

    @GetMapping("/download")
    public ResultData<byte[]>  readFile(@RequestBody Map map) throws IOException {

        String username = "/741917776";//假设当前用户为 741917776这个用户
        String fileName = (String) map.get("fileName");
        String path = ClassUtils.getDefaultClassLoader().getResource("public").getPath()+username+"/"+fileName;
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return ResultData.success(bytes);
    }




}
