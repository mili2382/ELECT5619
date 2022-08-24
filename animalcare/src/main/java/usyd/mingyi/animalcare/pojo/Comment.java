package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Integer id;
    private Integer postId;
    private Long commentTime;
    private String commentContent;
}
