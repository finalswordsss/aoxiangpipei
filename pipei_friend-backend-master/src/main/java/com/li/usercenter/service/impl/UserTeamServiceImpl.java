package com.li.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.usercenter.model.domain.UserTeam;
import com.li.usercenter.mapper.UserTeamMapper;
import com.li.usercenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author HP
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-06-14 11:37:20
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




