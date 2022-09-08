package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import usyd.mingyi.animalcare.utils.ImageUtil;

@SpringBootTest
class AnimalcareApplicationTests {



    @Test
    public void test(){
        System.out.println(ImageUtil.ImageToBase64ByLocal("D:/userdata/741917776/9afc1ca1-334d-4533-af54-e77ed9224d70.jpg"));

    }

}
