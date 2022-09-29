package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.service.PostService;


@SpringBootTest
class AnimalcareApplicationTests {

    @Autowired
    PetMapper petMapper;
    @Test
    public void test(){
        System.out.println(petMapper.myTest(1, 1));
    }

}
