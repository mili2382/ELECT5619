package usyd.mingyi.animalcare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("usyd.mingyi.animalcare.mapper")
public class AnimalcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimalcareApplication.class, args);
    }

}
