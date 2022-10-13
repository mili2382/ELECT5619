package usyd.mingyi.animalcare;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.CookieResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.Controller;
import usyd.mingyi.animalcare.config.LoginHandlerInterceptor;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.controller.LoginController;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
class AnimalcareApplicationTests {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    ProjectProperties projectProperties;
    @Autowired
    private WebApplicationContext wac;
    // Fake an MVC environment. The fake environment will not start tomcat
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session; //Get the session after login

    @Before
    public void setUp() { // Register mockmvc before testing, that is, declare MockMvc manually

        mockMvc = MockMvcBuilders.standaloneSetup(wac).build();
    }

    @Test
    @BeforeEach
    public void login() throws Exception {
        String url = "http://localhost:8080/login";
        MediaType JSONtype = MediaType.APPLICATION_JSON;//define data type as json

        //Test for non-existent users
        JSONObject badUser = new JSONObject();
        badUser.put("userName", "Lucas");
        badUser.put("password", "12345678");

        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(JSONtype)
                        .content(String.valueOf(badUser)).accept(JSONtype))
                .andExpect(content().contentType(JSONtype))
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andReturn();

        //Test for existing users
        JSONObject user = new JSONObject();
        user.put("userName", "richard");
        user.put("password", "12345678");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(JSONtype)
                        .content(String.valueOf(user)).accept(JSONtype))//Perform the requested
                .andExpect(content().contentType(JSONtype))//验证响应contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn();
        //result 结果用于如果有需要可以添加自定义assert
        session = (MockHttpSession) result.getRequest().getSession(false);

        Assert.assertEquals(2, session.getAttribute("id"));
        Assert.assertEquals("richard", session.getAttribute("userName"));

    }

    @Test
    public void signUp() throws Exception { //No new user is really added to the database because of @Transactional
        String url = "http://localhost:8080/signup";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json
        JSONObject newUser = new JSONObject();
        newUser.put("userName", "Test");
        newUser.put("email", "12345678@163.com");
        newUser.put("password", "11111111");

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(JSON)
                        .content(String.valueOf(newUser)).accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void uploadPost() throws Exception {
        String url = "http://localhost:8080/post/newPost";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json
        JSONArray base64Data = notNull();

        JSONObject newPost = new JSONObject();
        newPost.put("postTopic", "TopicTest");
        newPost.put("postContent", "Content Test");
        newPost.put("postTag", "cat");
        newPost.put("base64Data", base64Data);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .contentType(JSON).content(String.valueOf(newPost)).accept(JSON))
                .andExpect(content().contentType(JSON))//验证响应contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getPost() throws Exception {
        String url = "http://localhost:8080/getPost/{postId}";
        int postId = 1;
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, postId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getPostsByUserId() throws Exception {
        String url = "http://localhost:8080/getPostByUserId/{id}";
        int userId = 1;
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, userId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void deletePost() throws Exception {
        String url = "http://localhost:8080/post/deletePost/{postId}";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        //bad delete
        int BadPostId = 1;
        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.delete(url, BadPostId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Fail to delete post, No such post found"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, BadResult.getRequest().getSession().getAttribute("id"));

        //right delete
        int postId = 22;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url, postId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));


    }

    @Test
    public void addComment() throws Exception {
        String url = "http://localhost:8080/Post/addComment/{postId}";
        MediaType JSON = MediaType.APPLICATION_JSON;
        int postId = 1;

        JSONObject newComment = new JSONObject();
        newComment.put("commentContent", "JingyuTestComment");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url, postId)
                        .session(session)
                        .contentType(JSON).content(String.valueOf(newComment)).accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getCommentsByPostId() throws Exception {
        String url = "http://localhost:8080/getCommentsByPostId/{postId}";
        int postId = 1;
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, postId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void addPet() throws Exception { //不会往数据库添加新pet（@Transactional）
        String url = "http://localhost:8080/pet/newPet";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        JSONObject newPet = new JSONObject();
        newPet.put("avatarUrl", null);
        newPet.put("petName", "myTestDog");
        newPet.put("category", "dog");
        newPet.put("petImageListArr", null);
        newPet.put("petDescription", "myCuteTestDog");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .session(session)
                        .contentType(JSON).content(String.valueOf(newPet)).accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));

        if (session != null) {
            System.out.println("YESSSSS");
        }
    }

    @Test
    public void getPetList() throws Exception {
        String url = "http://localhost:8080/getPetList";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        JSONObject data = new JSONObject();//should be equal to data
        data.put("petId",3);
        data.put("userId", null);
        data.put("petName", "XIXI");
        data.put("age", 2);
        data.put("category", "dog");
        data.put("petImageAddress", "http://localhost:8080/images/dogDefault.jpg");
        data.put("petDescription", null);
        data.put("petImageList", null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void getPet() throws Exception {
        String url = "http://localhost:8080/pet/{petId}";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        //no pet found test
        int BadPetId = 1;
        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.get(url, BadPetId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("No such pet found"))//Validate Json content using Json path
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andDo(print())
                .andReturn(); //Return MvcResult
        Assert.assertEquals(2, BadResult.getRequest().getSession().getAttribute("id"));

        //exist pet
        int petId = 3;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url, petId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andReturn(); //Return MvcResult
        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    @Test
    public void deletePet() throws Exception {
        String url = "http://localhost:8080/pet/{petId}";
        MediaType JSON = MediaType.APPLICATION_JSON;//define data type as json

        //bad delete
        int BadPetId = 1;
        MvcResult BadResult = mockMvc.perform(MockMvcRequestBuilders.delete(url, BadPetId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Fail to delete for no such pet"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, BadResult.getRequest().getSession().getAttribute("id"));

        //right delete
        int petId = 3;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url, petId)
                        .session(session)
                        .accept(JSON))//Perform the requested
                .andExpect(content().contentType(JSON))//Verify the response contentType
                .andExpect(jsonPath("$.message").value("Success"))//Validate Json content using Json path
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); //Return MvcResult

        Assert.assertEquals(2, result.getRequest().getSession().getAttribute("id"));
    }

    //Redis test ----------------待删
    private final String RedisPostKey = "Test_postId: ";
    private final String RedisPostUserKey = "Test_post_userId: ";

    @Test
    public void RedisSet() {

        ValueOperations operations = redisTemplate.opsForValue(); //表明是以key，value形式储存到Redis数据库中
        operations.set("testKey", "RedisTest", 10, TimeUnit.MINUTES);
        operations.set("testKey2", "RedisTest2", 10, TimeUnit.MINUTES);
        operations.set("test_key", "test1", 100, TimeUnit.SECONDS);
        operations.set("test_key2", "test2", 100, TimeUnit.SECONDS);
    }

    @Test
    public void RedisDelete() {
        redisTemplate.delete("testKey");
    }

    @Test
    public void RedisGet() {
        ValueOperations operations = redisTemplate.opsForValue(); // 取的是key，value型的数据
        Object o1 = operations.get("testKey");
        Object o2 = operations.get("testKey2");
        System.out.println(o1 + " " + o2);
    }

    @Test
    public void redisGet2() {
        Set<String> keys = redisTemplate.keys("test_*");

        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            System.out.println(value);
        }
    }

    @Test
    public void postSet() {
        Post post = new Post();
        post.setPostId(100);
        post.setUserId(100);
        post.setPostContent("RedisTest1");
        post.setTopic("Test for same key");

        Post post1 = new Post();
        post1.setPostId(101);
        post1.setUserId(100);
        post1.setLove(10);
        post1.setTag("Cat");
        post1.setPosTime(System.currentTimeMillis());
        post1.setPostContent("RedisTest2");
        post1.setTopic("Test for same key2");

        redisTemplate.opsForValue().set(RedisPostKey + post.getPostId(), post, 200, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(RedisPostKey + post1.getPostId(), post1, 200, TimeUnit.SECONDS);

        redisTemplate.opsForValue().set(RedisPostUserKey + post.getUserId(), post, 200, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(RedisPostUserKey + post1.getUserId(), post1, 200, TimeUnit.SECONDS);

    }

    @Test
    public void postGet() {

        if (!redisTemplate.keys(RedisPostKey.concat("*")).isEmpty()) {
            Set<String> keys = redisTemplate.keys(RedisPostKey.concat("*"));
            List<Post> values = redisTemplate.opsForValue().multiGet(keys);
            System.out.println(values.size());
            for (Post post : values) {
                System.out.println(post);
            }
        }
    }

    @Test
    public void postGet2() {
        Set<String> keys = redisTemplate.keys("*");
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        System.out.println(redisTemplate.opsForValue().getOperations().getExpire(RedisPostKey + 100));

        for (Object v : values) {
            System.out.println(v);
        }
        redisTemplate.expire(RedisPostKey + 100, 200, TimeUnit.SECONDS);
    }

    @Test
    public void postGet3() {
        if (!redisTemplate.keys(RedisPostUserKey.concat("*")).isEmpty()) {
            Set<String> keys = redisTemplate.keys(RedisPostUserKey.concat("*"));
            System.out.println(keys.size());
            List<Post> posts = redisTemplate.opsForValue().multiGet(keys);
            for (Post post : posts) {
                System.out.println(post);
            }
        }
    }

    @Test
    public void getAllRedis() {

        Set<String> keys = redisTemplate.keys("*");
        List<Object> objects = redisTemplate.opsForValue().multiGet(keys);

        for (Object o : objects) {
            System.out.println(o);
        }
        System.out.println(keys.size());
    }

    @Test
    public void CommentSet() {
        Comment comment = new Comment();
        comment.setUserName("111");
        comment.setUserAvatar("222");
        comment.setId(100);
        comment.setUserId(111);
        comment.setCommentContent("test redis");
        comment.setCommentTime(System.currentTimeMillis());

        redisTemplate.opsForValue().set("testC", comment, 200, TimeUnit.SECONDS);

    }

    @Test
    public void commentGet() {
        Comment comment = (Comment) redisTemplate.opsForValue().get("testC");
        System.out.println(comment);
    }

    public final static String REDIS_POST_KEY = "postId: (Test)";
    public final static String REDIS_POST_USER_KEY = " <-post_userId, postId: (Test)";
    public final static String REDIS_COMMENT_KEY = " <- comment_postId, commentId: (Test)";
    public final static int TIMEOUT = 300;

    @Test
    public void getPostTest() {
        int id = 1;
        int postId = 1;

        if (redisTemplate.hasKey(REDIS_POST_KEY + postId)) {
            Post post = (Post) redisTemplate.opsForValue().get(REDIS_POST_KEY + postId);
            if (post != null) {
                redisTemplate.expire(REDIS_POST_KEY + postId, TIMEOUT, TimeUnit.SECONDS);
                boolean b = postService.checkLoved(id, postId);
                post.setLoved(b);
            }

            System.out.println("From redis");
            System.out.println(post);
        } else {
            Post post = postService.queryPostById(postId);

            if (post != null) {
                post = postService.queryPostById(postId);
                boolean b = postService.checkLoved(id, postId);
                post.setLoved(b);
                redisTemplate.opsForValue().set(REDIS_POST_KEY + postId, post, TIMEOUT, TimeUnit.SECONDS);

                System.out.println("From DB");
                System.out.println(post);
            }
        }
    }

    @Test
    public void testPetMap() {
        System.out.println(petMapper.myTest(1, 1));
    }
}
