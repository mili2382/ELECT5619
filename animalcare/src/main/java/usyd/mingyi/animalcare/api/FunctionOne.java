package usyd.mingyi.animalcare.api;

import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.utils.ResultData;

import java.util.List;

public interface FunctionOne {
    /**
     * 在home页面查询出所以用户的post
     * 从数据库返回至少8个Post的集合
     * 写对应算法 根据用户的标签过滤数据
     */
    ResultData<List<Post>> queryAllPost();

    /**
     * 用户发布一个post 将对应post的数据存在数据库
     * 需要把用户上传的jpg图片 保存到本地文件 已经提供工具类 详见 utils/FileStorage 也可以自己写
     * 传入的参数是一个文件和 post类的字段 可以用post类接收 成功返回true 失败 返回false
     */
    ResultData<Boolean> post(MultipartFile file, Post post);

    /**
     * 根据用户名返回另外一个用户的信息 返回类型为User
     * User中需要包含 所有的Posts Pets的对象集合
     *
     */
    ResultData<User> findUserByUsername(String userName);

}
