package usyd.mingyi.animalcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;


@SpringBootTest
class AnimalcareApplicationTests2 {


    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test() throws JSONException, org.json.JSONException {
        String url = "https://random-data-api.com/api/users/random_user";

        ResponseEntity<Object> result = restTemplate.getForEntity(url, Object.class);
        System.out.println("-----------------------");
        LinkedHashMap body = (LinkedHashMap) result.getBody();
        Object first_name = body.get("first_name");
        System.out.println(first_name.getClass());
        System.out.println(first_name);

    }

}
