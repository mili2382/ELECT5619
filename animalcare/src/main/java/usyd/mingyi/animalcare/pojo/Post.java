package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Integer postId;
    private Integer userId;
    private Integer love;
    private String postContent;

    private List<String> commentList;
    private List<String> videoUrlList;
    private List<String> imageUrlList;

}
