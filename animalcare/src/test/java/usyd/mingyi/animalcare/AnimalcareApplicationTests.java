package usyd.mingyi.animalcare;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import usyd.mingyi.animalcare.pojo.User;

import java.util.LinkedHashMap;


@SpringBootTest
class AnimalcareApplicationTests {


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
