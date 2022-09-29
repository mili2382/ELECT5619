package usyd.mingyi.animalcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.pojo.Comment;
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
    public List<Post> getAllPosts(int currPage, int pageSize) {
        return postMapper.getAllPosts(currPage,pageSize);
    }

    @Override
    public Post queryPostById(int postId) {
        return postMapper.queryPostById(postId);
    }

    @Override
    public boolean checkLoved(int userId, int postId) {
        return postMapper.checkLoved(userId,postId);
    }

    @Override
    @Transactional
    public int love(int userId, int postId) {
        postMapper.lovePlus(postId);
        return postMapper.love(userId,postId);
    }

    @Override
    @Transactional
    public int cancelLove(int userId, int postId) {
        postMapper.loveMinus(postId);
        return postMapper.cancelLove(userId,postId);
    }

    @Override
    public int lovePlus(int postId) {
        return postMapper.lovePlus(postId);
    }

    @Override
    public int loveMinus(int postId) {
        return postMapper.loveMinus(postId);
    }

    @Override
    public int deletePost(int postId, int userId) {
        return postMapper.deletePost(postId, userId );
    }

    @Override
    public int addComment(Comment comment) {
       return postMapper.addComment(comment);
    }


    @Override
    public List<Post> getPostByUserId(int userId) {
        return postMapper.getPostsByUserId(userId);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        return postMapper.getCommentsByPostId(postId);
    }

}
