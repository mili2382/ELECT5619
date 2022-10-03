package usyd.mingyi.animalcare.controller;


import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.ImageUtil;
import usyd.mingyi.animalcare.utils.JasyptEncryptorUtils;
import usyd.mingyi.animalcare.utils.ResultData;
import usyd.mingyi.animalcare.utils.Verification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
@CrossOrigin
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    PetService petService;

    @Autowired
    private RedisTemplate redisTemplate;

    public final static String FILE_DISK_LOCATION = "D://userdata/";
    public final static String PROJECT_PREFIX = "localhost:8080/images/";


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
            session.setAttribute("nickName", user.getNickName());
            session.setAttribute("userAvatar", user.getUserImageAddress());
            redisTemplate.opsForValue().set("user", user, 300, TimeUnit.SECONDS);
            if (redisTemplate.hasKey("user")) {
                System.out.println(redisTemplate.opsForValue().get("user"));
            }
            return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);

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
        userService.sendEmail(email, userName);
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
    public ResponseEntity<Object> updateUserInfo(@RequestBody User userInfo, HttpSession session) {
        int id = (int) session.getAttribute("id");
        String userName = (String) session.getAttribute("userName");
        userInfo.setId(id);
        String avatarUrl = userInfo.getUserImageAddress();
        if (!StringUtil.isNullOrEmpty(avatarUrl) && ImageUtil.checkImage(avatarUrl)) {
            //更改照片
            String suffix = ImageUtil.getSuffix(avatarUrl);
            String data = ImageUtil.getData(avatarUrl);
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = FILE_DISK_LOCATION + userName; //文件路径
            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                userInfo.setUserImageAddress(PROJECT_PREFIX + userName + "/" + tempFileName);
                userService.updateUser(userInfo);
                //删掉本地之前的头像（未写）
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }
        } else {
            //查到之前头像的address
            User user = userService.queryUserById(id);
            String preUserImageAddress = user.getUserImageAddress();
            userInfo.setUserImageAddress(preUserImageAddress);
            userService.updateUser(userInfo);
        }
        return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);


    }

/*
    @GetMapping("/getPetList")
    @ResponseBody
    public ResultData<List<Integer>> getPetList() {
//        TODO: get pet from database
        System.out.println("getting pet list");
        List<Integer> pets = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            pets.add(i);
        }
        return ResultData.success(pets);
    }

    @GetMapping("/getFriendList")
    @ResponseBody
    public ResultData<List<Integer>> getFriendList() {
//        TODO: get friends from database
        System.out.println("getting friend list");
        List<Integer> friends = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            friends.add(i);
        }
        return ResultData.success(friends);
    }

    @GetMapping("/getFriendRequestList")
    @ResponseBody
    public ResultData<List<Integer>> getFriendRequestList() {
//        TODO: get friends from database
        System.out.println("getting friend request list");
        List<Integer> friends = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            friends.add(i);
        }
        return ResultData.success(friends);
    }

    @PostMapping("/post/newPet")
    @ResponseBody
    public ResponseEntity<Object> createNewPet(@RequestBody Map map, HttpServletRequest request) {
        String petName = (String) map.get("petName");
        String petDescription = (String) map.get("petDescription");
        ArrayList<String> list = (ArrayList<String>) map.get("petImageAddress");
        // TODO: add more logics
        return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);
    }*/

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
                postService.addImage(postId, PROJECT_PREFIX + userName + "/" + tempFileName);

            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);

            }
        }

        return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);

    }

    @GetMapping("/getPosts")
    @ResponseBody
    public ResponseEntity<Object> getPosts(@RequestParam("currPage") int currPage, @RequestParam("pageSize") int pageSize) {
        List<Post> allPosts = postService.getAllPosts(currPage, pageSize);
        return new ResponseEntity<>(ResultData.success(allPosts), HttpStatus.OK);
    }
    @GetMapping("/getPostsOrderByLove")
    @ResponseBody
    public ResponseEntity<Object> getPostsOrderByLove(@RequestParam("currPage") int currPage, @RequestParam("pageSize") int pageSize) {
        List<Post> allPosts = postService.getAllPostsOrderByLove(currPage, pageSize);
        return new ResponseEntity<>(ResultData.success(allPosts), HttpStatus.OK);
    }


    //采用Restful风格进行一次传参
    @GetMapping("/getPost/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getPost(@PathVariable int postId, HttpServletRequest request) {

        HttpSession session = request.getSession();
        int id = (int) session.getAttribute("id");
        Post post = postService.queryPostById(postId);
        if (post != null) {
            post = postService.queryPostById(postId);
            boolean b = postService.checkLoved(id, postId);
            post.setLoved(b);
            return new ResponseEntity<>(ResultData.success(post), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "No such post found"), HttpStatus.CREATED);
        }
    }


    @GetMapping("/love/{postId}")
    public ResponseEntity<Object> love(@PathVariable("postId") int postId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        postService.love(id, postId);
        //postService.lovePlus(postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @DeleteMapping("/love/{postId}")
    public ResponseEntity<Object> cancelLove(@PathVariable("postId") int postId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        postService.cancelLove(id, postId);
        //postService.loveMinus(postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @DeleteMapping("/post/deletePost/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable("postId") int postId, HttpSession session) {

        int id = (int) session.getAttribute("id");
        Post post = postService.queryPostById(postId);
        if (postService.deletePost(postId, id) == 0) {
            return new ResponseEntity<>(ResultData.fail(201, "Fail to delete post, No such post found"), HttpStatus.CREATED);
        } else {
            System.out.println("Delete post" + postId);
            //postService.deletePost(postId, id);
            return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
        }

    }

    @PostMapping("/Post/addComment/{postId}")
    @ResponseBody
    public ResponseEntity<Object> addComment(@PathVariable("postId") int postId, @RequestBody Map map, HttpServletRequest request) {

        HttpSession session = request.getSession();
        String commentContent = (String) map.get("commentContent");
        String nickName = (String) session.getAttribute("nickName");
        String userAvatar = (String) session.getAttribute("userAvatar");

        Comment comment = new Comment();
        comment.setCommentContent(commentContent);
        comment.setNickName(nickName);
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserAvatar(userAvatar);

        if (commentContent == null) {
            return new ResponseEntity<>(ResultData.fail(201, "Comment can not be null"), HttpStatus.CREATED);
        }
        if (postService.addComment(comment) != 1) {
            return new ResponseEntity<>(ResultData.fail(201, "Comment invalid"), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(ResultData.success("Comment Added"), HttpStatus.OK);
    }

    @GetMapping("/getPostByUserId/{id}")
    @ResponseBody
    public ResponseEntity<Object> getPostsByUserId(@PathVariable("id") int userId, HttpServletRequest request) {

        HttpSession session = request.getSession();

        User user = userService.queryUserById(userId);
        if (user == null) {
            return new ResponseEntity<>(ResultData.fail(201, "No such user"), HttpStatus.CREATED);
        } else {
//            List<Post> tempList = postService.getPostByUserId(userId);
//            int i = 0;
//            for(Post post: tempList) {
//                post = ImageUtil.replaceUrl(postService.getPostByUserId(userId).get(i), FILE_DISK_LOCATION);
//                PostsByUserId.add(post);
//                i++;
//            }
            List<Post> PostsByUserId = postService.getPostByUserId(userId);

            return new ResponseEntity<>(ResultData.success(PostsByUserId), HttpStatus.OK);
        }
    }

    @GetMapping("/getCommentsByPostId/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getCommentsByPostId(@PathVariable("postId") int postId, HttpServletRequest request) {


        if (postService.queryPostById(postId) == null) {
            return new ResponseEntity<>(ResultData.fail(201, "No such post"), HttpStatus.CREATED);
        } else {

            List<Comment> CommentsByPostId = postService.getCommentsByPostId(postId);
            System.out.println(CommentsByPostId);

            return new ResponseEntity<>(ResultData.success(CommentsByPostId), HttpStatus.OK);
        }
    }

    @PostMapping("/pet/newPet")
    public ResponseEntity<Object> addPet(@RequestBody Map map, HttpSession session) {
        int id = (int) session.getAttribute("id");
        String userName = (String) session.getAttribute("userName");
        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据
        String avatarUrl = (String) map.get("avatarUrl");
        String name = (String) map.get("petName");
        String category = (String) map.get("category");
        List<String> list = (List<String>) map.get("petImageListArr");
        String petDescription = (String) map.get("petDescription");
        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setPetName(name);
        pet.setUserId(id);
        pet.setPetDescription(petDescription);

        if (StringUtil.isNullOrEmpty(avatarUrl)) {
            if (category.equals("dog")) {
                pet.setPetImageAddress(PROJECT_PREFIX + "dogDefault.jpg");
            } else {
                pet.setPetImageAddress(PROJECT_PREFIX + "catDefault.jpg");
            }


        } else if (ImageUtil.checkImage(avatarUrl)) {
            suffix = ImageUtil.getSuffix(avatarUrl);
            data = ImageUtil.getData(avatarUrl);
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = FILE_DISK_LOCATION + userName; //文件路径
            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                pet.setPetImageAddress(PROJECT_PREFIX + userName + "/" + tempFileName);


            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
        }
        Integer petId = petService.addPet(pet);
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
                petService.addImage(petId, PROJECT_PREFIX + userName + "/" + tempFileName);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);

            }
        }

        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @GetMapping("/getPetList")
    public ResponseEntity<Object> getPetList(HttpSession session) {
        int id = (int) session.getAttribute("id");
        List<Pet> petList = petService.getPetList(id);
        return new ResponseEntity<>(ResultData.success(petList), HttpStatus.OK);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<Object> getPet(@PathVariable("petId") int petId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        Pet pet = petService.getPet(petId, id);

        if (pet != null) {
            return new ResponseEntity<>(ResultData.success(pet), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "No such pet found"), HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/pet/{petId}")
    public ResponseEntity<Object> deletePet(@PathVariable("petId") int petId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        int i = petService.deletePet(petId, id);
        if (i == 0) return new ResponseEntity<>(ResultData.fail(201, "Fail to delete for no such pet"), HttpStatus.OK);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Object> getProfile(@PathVariable("userId") int userId, HttpSession session) {

        User profile = userService.getProfile(userId);

        return new ResponseEntity<>(ResultData.success(profile), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getMyProfile(HttpSession session) {
        int id = (int) session.getAttribute("id");
        User profile = userService.getProfile(id);
        return new ResponseEntity<>(ResultData.success(profile), HttpStatus.OK);
    }


    @GetMapping("/search/{keywords}")
    @ResponseBody
    public ResponseEntity<Object> getPosts(@PathVariable("keywords") String keywords) {
        keywords = keywords + "*";
        List<Post> postsByKeywords = postService.getPostsByKeywords(keywords);
        return new ResponseEntity<>(ResultData.success(postsByKeywords), HttpStatus.OK);
    }


}
