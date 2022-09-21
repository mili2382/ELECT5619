package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.api.InterfaceTwo;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

@Mapper
public interface PostMapper {
    int addPost(Post post);
    int addImage(int imagePostId,String imageUrl);
    List<Post> getAllPosts(int currPage, int pageSize);
    Post queryPostById(int postId);
    boolean checkLoved(int userId,int postId);
    int love(int userId, int postId);
    int cancelLove(int userId, int postId);
    int lovePlus(int postId);
    int loveMinus(int postId);
    int deletePost(int postId);
    int addComment(int postId);
    List<Post> getPostsByUserId(int userId);
}
