package com.xja.wechat.dao;

import java.util.List;

import com.xja.wechat.bean.MyTeam;
import com.xja.wechat.entity.PullUser;

public interface PullUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PullUser record);

    int insertSelective(PullUser record);

    PullUser selectByPrimaryKey(Integer id);
    
    List<MyTeam> selectMyTeam(String openid);
    
    Integer selectMyTeamCount(String openid);

    int updateByPrimaryKeySelective(PullUser record);

    int updateByPrimaryKey(PullUser record);
}