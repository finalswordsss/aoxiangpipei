package com.li.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li.usercenter.model.domain.Team;
import com.li.usercenter.model.domain.User;
import com.li.usercenter.model.domain.dto.TeamQuery;
import com.li.usercenter.model.domain.request.TeamJoinRequest;
import com.li.usercenter.model.domain.request.TeamQuitRequest;
import com.li.usercenter.model.domain.request.TeamUpdateRequest;
import com.li.usercenter.model.domain.vo.TeamUserVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author HP
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-06-14 11:34:40
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍，依据vo提供的要素
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    boolean deleteTeam(long id, User loginUser);
}
