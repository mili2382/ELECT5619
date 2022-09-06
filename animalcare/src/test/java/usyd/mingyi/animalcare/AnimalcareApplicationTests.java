package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.JasyptEncryptorUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class AnimalcareApplicationTests {



    @Test
    public void test(){
        String password = "123456";
        String encode = JasyptEncryptorUtils.encode(password);
        String decode = JasyptEncryptorUtils.decode(encode);

    }

}
