package usyd.mingyi.animalcare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.pojo.Comment;
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
import java.util.concurrent.TimeUnit;


@Controller
@CrossOrigin
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    private RedisTemplate redisTemplate;

    public final static String FILE_DISK_LOCATION = "userdata/";


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

            redisTemplate.opsForValue().set("user",user,300,TimeUnit.SECONDS);
            if(redisTemplate.hasKey("user")){
                System.out.println(redisTemplate.opsForValue().get("user"));
            }
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
            return new ResponseEntity<>(ResultData.fail(201, "Signup fail"), HttpStatus.CREATED);
        }

    }

    @GetMapping("/username")
    @ResponseBody
    public ResponseEntity<Object> usernameCheck(@RequestParam("userName") String userName) {

        User user = userService.queryUserByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>(ResultData.success(null), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultData.fail(201, "Fail"), HttpStatus.CREATED);

    }

    @PostMapping("/email")
    @ResponseBody
    public ResponseEntity<Object> sendEmailByUsername(@RequestBody Map map) {
        String email = (String) map.get("email");
        String userName = (String) map.get("userName");
        userService.sendEmail(email,userName);
        return new ResponseEntity<>(ResultData.success(null), HttpStatus.OK);
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
                return new ResponseEntity<>(ResultData.fail(201, "Code not equal"), HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(ResultData.fail(201, "No code in the system"), HttpStatus.CREATED);
    }


    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<Object> updateUserInfo(@RequestBody User userInfo) {

        int i = userService.updateUser(userInfo);
        if (i >= 1) {
            return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(ResultData.fail(201, "Update fail"), HttpStatus.CREATED);

        }

    }


    @GetMapping("/getPetList")
    @ResponseBody
    public ResultData<List<Map<String, String>>> getPetList() {
//        TODO: get pet from database
        System.out.println("getting pet list");
        List<Map<String, String>> petList = new ArrayList<>();
        Map<String, String> pet1 = new HashMap<>();
        pet1.put("name", "abc");
        pet1.put("category", "cat");
        pet1.put("petAvatar", "domestic-cat_thumb.webp");
        pet1.put("petDescription", "haha");
        petList.add(pet1);

        Map<String, String> pet2 = new HashMap<>();
        pet2.put("name", "def");
        pet2.put("category", "dog");
        pet2.put("petAvatar", "domestic-cat_thumb.webp");
        pet2.put("petDescription", "hehe");
        petList.add(pet2);
        return ResultData.success(petList);
    }

    @GetMapping("/getFriendList")
    @ResponseBody
    public ResultData<List<Map<String, String>>> getFriendList(HttpSession session) {
        //        TODO: get friends from database
        String userName = (String) session.getAttribute("userName");
        System.out.println("getting friend list");

        List<Map<String, String>> friendList = new ArrayList<>();
        Map<String, String> friend1 = new HashMap<>();
        friend1.put("name", "abc");
        friend1.put("description", "111");
        friend1.put("userAvatar", "222");
        friendList.add(friend1);

        Map<String, String> friend2 = new HashMap<>();
        friend2.put("name", "def");
        friend2.put("description", "333");
        friend2.put("userAvatar", "444");
        friendList.add(friend2);
        return ResultData.success(friendList);
    }

    @GetMapping("/getFriendRequestList")
    @ResponseBody
    public ResultData<List<Map<String, String>>> getFriendRequestList() {
//        TODO: get friends request from database
        System.out.println("getting friend request list");
        List<Map<String, String>> friendRequestList = new ArrayList<>();
        Map<String, String> request1 = new HashMap<>();
        request1.put("userName", "abc");
        request1.put("requestText", "hello");
        request1.put("userAvatar", "222");
        friendRequestList.add(request1);

        Map<String, String> request2 = new HashMap<>();
        request2.put("userName", "def");
        request2.put("requestText", "hello");
        request2.put("userAvatar", "444");
        friendRequestList.add(request2);
        return ResultData.success(friendRequestList);
    }

    @PostMapping("/post/newPet")
    @ResponseBody
    public ResponseEntity<Object> createNewPet(@RequestBody Map map, HttpServletRequest request) {
        String petName = (String) map.get("petName");
        String petDescription = (String) map.get("petDescription");
        ArrayList<String> list = (ArrayList<String>) map.get("petImageAddress");
        // TODO: add more logics
        return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);
    }

    @PostMapping("/post/newPost")
    @ResponseBody
    public ResponseEntity<Object> upLoadPost(@RequestBody Map map, HttpServletRequest request) {

        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据

        ArrayList<String> list = (ArrayList<String>) map.get("base64Data");
        String postTopic = (String) map.get("postTopic");
        String postContent = (String) map.get("postContent");
        String postTag = (String) map.get("postTag");
        HttpSession session = request.getSession();

        String userName = (String) session.getAttribute("userName");
        int id = (int) session.getAttribute("id");

        Post post = new Post();
        post.setUserId(id);
        post.setLove(0);
        post.setPosTime(System.currentTimeMillis());
        post.setPostContent(postContent);
        post.setTopic(postTopic);
        post.setTag(postTag);
        if (postService.addPost(post) != 1) {
            return new ResponseEntity<>(ResultData.fail(201, "Content invalid"), HttpStatus.CREATED);
        }
        Integer postId = post.getPostId();

        for (String base64Data : list) {
            if (ImageUtil.checkImage(base64Data)) {
                suffix = ImageUtil.getSuffix(base64Data);
                data = ImageUtil.getData(base64Data);
            } else {
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }


            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名


            String path = FILE_DISK_LOCATION + userName; //文件路径

            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                postService.addImage(postId, userName+"/"+tempFileName);

            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);

            }
        }

        return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);

    }

    @GetMapping("/getPosts")
    @ResponseBody
    public ResponseEntity<Object> getPosts(HttpSession session,@RequestParam("currPage") int currPage, @RequestParam("pageSize") int pageSize) {
        System.out.println("get post: " + currPage + pageSize);
        String userName = (String) session.getAttribute("userName");
        List<Post> allPosts = ImageUtil.replaceUrl(postService.getAllPosts(currPage,pageSize), FILE_DISK_LOCATION );
        return new ResponseEntity<>(ResultData.success(allPosts), HttpStatus.OK);
    }


    //采用Restful风格进行一次传参
    @GetMapping("/getPost/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getPost(@PathVariable int postId,HttpServletRequest request){

        HttpSession session = request.getSession();
        int id = (int) session.getAttribute("id");
        Post post = postService.queryPostById(postId);
        if(post!=null){
            post = ImageUtil.replaceUrl(postService.queryPostById(postId), FILE_DISK_LOCATION );
            boolean b = postService.checkLoved(id, postId);
            post.setLoved(b);
            return new ResponseEntity<>(ResultData.success(post), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(ResultData.fail(201,"No such post found"), HttpStatus.CREATED);
        }
    }



    /*根据图片名字取到对应的图片 暂时不用*/
    @GetMapping("/getImage")
    public ResponseEntity<Object> getImage(@RequestParam("imageName") String imageName, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("userName");
        String type = imageName.substring(imageName.lastIndexOf(".") + 1);
        System.out.println(type);

        String path = FILE_DISK_LOCATION + userName + File.separator + imageName;
        System.out.println(path);
        File file = new File(path);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(ResultData.fail(201, "No such file"), HttpStatus.CREATED);
        }

        byte[] bytesByStream = ImageUtil.getBytesByStream(inputStream);

        final HttpHeaders headers = new HttpHeaders();
        if (type.equalsIgnoreCase("jpg")) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else if (type.equalsIgnoreCase("png")) {
            headers.setContentType(MediaType.IMAGE_PNG);
        } else if (type.equalsIgnoreCase("gif")) {
            headers.setContentType(MediaType.IMAGE_GIF);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "Image invalid"),HttpStatus.CREATED);
        }

        return new ResponseEntity<>(bytesByStream, headers, HttpStatus.OK);

    }

    @GetMapping("/love/{postId}")
    public ResponseEntity<Object> love(@PathVariable("postId") int postId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        postService.love(id, postId);
        postService.lovePlus(postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }
    @DeleteMapping("/love/{postId}")
    public ResponseEntity<Object> cancelLove(@PathVariable("postId") int postId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        postService.cancelLove(id, postId);
        postService.loveMinus(postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Object> deletePost(@PathVariable("postId") int postId, HttpSession session) {

        System.out.println("Delete post" + postId);
        //session
        postService.deletePost(postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> addComment(@PathVariable("postId") int postId, HttpSession session) {
        System.out.println("Add comment to" + postId);
        //session
        postService.addComment(postId);
        return new ResponseEntity<>(ResultData.success("Comment Added"), HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> getPostsByUserId(int userId, HttpServletRequest request) {
        System.out.println("Getting posts by" + userId);
        //session
        List<Post> PostsByUserId = userService.queryUserById(userId).getPostList();
        if(userService.queryUserById(userId) == null) {
            return new ResponseEntity<>(ResultData.fail(201,"No such user"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(ResultData.success(PostsByUserId), HttpStatus.OK);
    }

//    @GetMapping
//    @ResponseBody
//    public ResponseEntity<Object> getCommentsByPostId(@PathVariable("postId") int postId, HttpRequest request) {
//        System.out.println("Getting comments by" + postId);
//        //session
//        if(postService.queryPostById(postId) == null) {
//            return new ResponseEntity<>(ResultData.fail(201,"No such post"), HttpStatus.CREATED);
//        }
//        List<Comment> CommentsByPostId = postService.queryPostById(postId).getCommentList();
//        return new ResponseEntity<>(ResultData.success(CommentsByPostId), HttpStatus.OK);
//    }



}
