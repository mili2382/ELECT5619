package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.service.PostService;


@SpringBootTest
class AnimalcareApplicationTests {


    @Autowired
    ProjectProperties projectProperties;
 /*   private  String FILE_DISK_LOCATION = projectProperties.getFileDiskLocation();
    private   String PROJECT_PREFIX = projectProperties.getProjectPrefix();*/
    @Test
    public void test(){
/*        System.out.println(FILE_DISK_LOCATION);
        System.out.println(PROJECT_PREFIX);*/
        System.out.println(projectProperties.toString());
    }

}
