package usyd.mingyi.animalcare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.ImageUtil;
import usyd.mingyi.animalcare.utils.JasyptEncryptorUtils;
import usyd.mingyi.animalcare.utils.ResultData;
import usyd.mingyi.animalcare.utils.Verification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;


@Controller
@CrossOrigin
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JavaMailSender mailSender;

    public final static String FILE_DISK_LOCATION = "D:/userdata/";


    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Object> login(@RequestBody User userInfo, HttpServletRequest request) {


        String username = userInfo.getUserName();
        String password = userInfo.getPassword();
        String encryptedPassword = userService.queryPassword(username);

        if (encryptedPassword == null) {
            return new ResponseEntity<>(ResultData.fail(401, "No such user"), HttpStatus.UNAUTHORIZED);
        } else {
            String decode = JasyptEncryptorUtils.decode(encryptedPassword);
            if (!decode.equals(password)) {
                return new ResponseEntity<>(ResultData.fail(401, "Password error"), HttpStatus.UNAUTHORIZED);

            }
        }

        User user = userService.queryUser(username, encryptedPassword);


        if (user != null) {

            HttpSession session = request.getSession();
            session.setAttribute("id", user.getId());
            session.setAttribute("userName", user.getUserName());

            return new ResponseEntity<>(ResultData.success(user), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(ResultData.fail(401, "Password error"), HttpStatus.UNAUTHORIZED);

        }


    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Object> signup(@RequestBody User userInfo) {

        userInfo.setPassword(JasyptEncryptorUtils.encode(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        int i = userService.addUser(userInfo);
        if (i >= 1) {
            return new ResponseEntity<>(ResultData.success("Signup success"), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(ResultData.fail(401, "Signup fail"), HttpStatus.BAD_REQUEST);

        }

    }

    @GetMapping("/username")
    @ResponseBody
    public ResponseEntity<Object> usernameCheck(@RequestBody Map map) {
        String userName = (String) map.get("userName");
        User user = userService.queryUserByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>(ResultData.success(true), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultData.fail(400, "Fail"), HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/email")
    @ResponseBody
    public ResponseEntity<Object> sendEmailByUsername(@RequestBody Map map) {
        String email = (String) map.get("email");
        String userName = (String) map.get("userName");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("Verification Code");
        int i = new Random().nextInt(1000000);
        String code = String.format("%06d", i);
        mailMessage.setText("This is your one time verification code :" + code);
        mailMessage.setTo(email);
        mailMessage.setFrom("LMY741917776@gmail.com");
        mailSender.send(mailMessage);
        Verification.putCode(userName, code);
        return new ResponseEntity<>(ResultData.success("Send success"), HttpStatus.OK);
    }

    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<Object> validateCode(@RequestBody Map map) {
        String code = (String) map.get("code");
        String userName = (String) map.get("userName");
        if (Verification.hasUser(userName)) {
            if (Verification.getCode(userName).equals(code)) {
                return new ResponseEntity<>(ResultData.success("Code equal"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResultData.fail(400, "Code not equal"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(ResultData.fail(400, "No code in the system"), HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<Object> updateUserInfo(@RequestBody User userInfo) {

        int i = userService.updateUser(userInfo);
        if (i >= 1) {
            return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(ResultData.fail(400, "Update fail"), HttpStatus.BAD_REQUEST);

        }

    }



    @GetMapping("/get-pet-list")
    @ResponseBody
    public ResultData<List<Integer>> getPetList() throws IOException {
//        TODO: get friends from database
        System.out.println("getting pet list");
        List<Integer> friends = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            friends.add(i);
        }
        return ResultData.success(friends);
    }


    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Object> upLoadPost(@RequestBody Map map, HttpServletRequest request) {

        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据

        ArrayList<String> list = (ArrayList<String>)map.get("base64Data");



        HttpSession session = request.getSession();

        String userName = (String) session.getAttribute("userName");
        int id = (int) session.getAttribute("id");

        Post post = new Post();
        post.setUserId(id);
        post.setLove(0);
        post.setPosTime(System.currentTimeMillis());
        post.setPostContent("Test post");
        if( postService.addPost(post)!=1){
            return new ResponseEntity<>(ResultData.fail(204, "Content invalid"), HttpStatus.NO_CONTENT);
        }
        Integer postId = post.getPostId();

        for (String base64Data : list) {
            if(ImageUtil.checkImage(base64Data)){
                suffix=ImageUtil.getSuffix(base64Data);
                data=ImageUtil.getData(base64Data);
            }else {
                return new ResponseEntity<>(ResultData.fail(204, "File invalid"), HttpStatus.NO_CONTENT);
            }


                String tempFileName = UUID.randomUUID().toString() + suffix; //文件名


                String path = FILE_DISK_LOCATION+userName; //文件路径
                System.out.println(path);
                try {
                    ImageUtil.convertBase64ToFile(data,path,tempFileName);
                    postService.addImage(postId,tempFileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(ResultData.fail(204, "File invalid"), HttpStatus.NO_CONTENT);

                }
            }

            return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);

        }

        @GetMapping("/getPosts")
        @ResponseBody
        public ResponseEntity<Object> getPosts() {

            List<Post> allPosts = postService.getAllPosts();

            return new ResponseEntity<>(ResultData.success(allPosts), HttpStatus.OK);
        }


        @GetMapping("/getImage")
        public ResponseEntity<Object> getImage(@RequestParam("imageName") String imageName,HttpServletRequest request)  {
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");
            String type = imageName.substring(imageName.lastIndexOf(".")+1);
            System.out.println(type);

            String path = FILE_DISK_LOCATION+userName+ File.separator+imageName;
            System.out.println(path);
            File file = new File(path);
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                return new ResponseEntity<>(ResultData.fail(204, "No such file"), HttpStatus.NO_CONTENT);
            }

            byte[] bytesByStream = ImageUtil.getBytesByStream(inputStream);

            final HttpHeaders headers = new HttpHeaders();
            if(type.equalsIgnoreCase("jpg")){
                headers.setContentType(MediaType.IMAGE_JPEG);
            }else if(type.equalsIgnoreCase("png")){
                headers.setContentType(MediaType.IMAGE_PNG);
            }else if(type.equalsIgnoreCase("gif")){
                headers.setContentType(MediaType.IMAGE_GIF);
            }else {
                return new ResponseEntity<>(ResultData.fail(204, "Image invalid"), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(bytesByStream,headers, HttpStatus.OK);

        }


    }
