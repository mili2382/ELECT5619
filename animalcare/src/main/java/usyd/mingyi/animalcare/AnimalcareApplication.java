package usyd.mingyi.animalcare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
/*@MapperScan("usyd.mingyi.animalcare.mapper")
@ComponentScan({"usyd.mingyi.animalcare.controller","usyd.mingyi.animalcare.service"})*/
public class AnimalcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimalcareApplication.class, args);
    }

}
