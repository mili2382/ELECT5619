package usyd.mingyi.animalcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

@Service
public class PostServiceImp implements PostService{
    @Autowired
    PostMapper postMapper;

    @Override
    public int addPost(Post post) {
       return postMapper.addPost(post);
    }

    @Override
    public int addImage(int imagePostId, String imageUrl) {
        return postMapper.addImage(imagePostId,imageUrl);
    }

    @Override
    public List<Post> getAllPosts() {
        return postMapper.getAllPosts();
    }
}
