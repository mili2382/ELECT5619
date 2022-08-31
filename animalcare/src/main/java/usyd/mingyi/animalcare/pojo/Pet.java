package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    private Integer petId;
    private Integer userId;
    private String petName;
    private Integer age;
    private String category;
    private String petImageAddress;

}
