package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
class AnimalcareApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test(){
      redisTemplate.opsForValue().set("111","11111");
        Object o = redisTemplate.opsForValue().get("111");
        System.out.println(o);
    }

}
