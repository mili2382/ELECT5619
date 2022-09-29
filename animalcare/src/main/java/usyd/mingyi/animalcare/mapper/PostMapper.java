package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
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
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
    int deletePost(int postId);
    int addComment(Comment comment);
=======
>>>>>>> Stashed changes
    int deletePost(int postId, int userId);
    int addComment(int postId);
>>>>>>> 3a92d38c9739f91c3486b7c58e24ee0706c8a954
    List<Post> getPostsByUserId(int userId);
    List<Comment> getCommentsByPostId(int postId);
}
