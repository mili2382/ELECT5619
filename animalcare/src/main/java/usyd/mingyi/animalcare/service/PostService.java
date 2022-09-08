package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

public interface PostService {
    int addPost(Post post);
    int addImage(int imagePostId,String imageUrl);
    List<Post> getAllPosts();
}
