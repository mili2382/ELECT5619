package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import usyd.mingyi.animalcare.service.PostService;


@SpringBootTest
class AnimalcareApplicationTests {

    @Autowired
    PostService postService;


    @Test
    public void test(){
        boolean b = postService.checkLoved(1, 1);
        System.out.println(b);
    }

}
