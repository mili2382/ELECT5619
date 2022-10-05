package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Mapper
public interface FriendMapper {
    int checkExistRequest(int fromId,int toId);// 检查是否自己已经发过同样的请求
    int checkRequestReverse(int fromId,int toId);//检查是否被该用户请求添加过
    int sendFriendRequest(int fromId,int toId);//发送到对方请求列表中
    int deleteFriendRequest(int fromId,int toId);//删除无效请求

    int addToFriendList(int fromId,int toId);//互相添加到好友列表

    boolean isFriend(int fromId,int toId);//判断是否已经是好友了

    List<User> getAllFriends(int id);//根据用户id获取用户所有的friends

    List<User> getAllRequests(int id);//根据用户id获取用户所有的request



}
