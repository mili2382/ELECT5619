package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

@Mapper
public interface PostMapper {
    int addPost(Post post);
    int addImage(int imagePostId,String imageUrl);
    List<Post> getAllPosts();
    Post queryPostById(int postId);
}
